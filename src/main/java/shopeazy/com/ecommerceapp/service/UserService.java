package shopeazy.com.ecommerceapp.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import shopeazy.com.ecommerceapp.exceptions.InvalidEmailException;
import shopeazy.com.ecommerceapp.model.*;
import shopeazy.com.ecommerceapp.model.dto.CreateUserRequest;
import shopeazy.com.ecommerceapp.repository.PermissionRepository;
import shopeazy.com.ecommerceapp.repository.RoleRepository;
import shopeazy.com.ecommerceapp.repository.UserRepository;

import java.time.Instant;
import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, RoleRepository roleRepository, PermissionRepository permissionRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /*
    1. Unique email (done)
    2. password restrictions (done) / should use password encoder here
    3.
     */

    public User create(CreateUserRequest createUserRequest) {
        if (!userRepository.existsByEmail(createUserRequest.getEmail())) {
            Role userRole = defaultRoleAssignmentWithDefaultPermission();

            User user = new User();

            user.setEmail(createUserRequest.getEmail());
            user.setPassword(passwordEncoder.encode(createUserRequest.getPassword().trim()));
            user.setFirstName(createUserRequest.getFirstName());
            user.setLastName(createUserRequest.getLastName());
            user.setGender(createUserRequest.getGender());
            user.setRoles(List.of(userRole));
            user.setStatus(Status.ACTIVE);
            user.setOrdersCount(0);
            user.setCreatedAt(Instant.now());
            user.setUpdatedAt(Instant.now());

            user.setAddress(null);
            return userRepository.save(user);
        } else {
            throw new InvalidEmailException("Email already exists");
        }
    }

    private Role defaultRoleAssignmentWithDefaultPermission() {
        // Check if the "READ" permission exists, or create it if it doesn't
        Permission readPermission = permissionRepository.findByName("READ")
                .orElseGet(() -> permissionRepository.save(new Permission("READ")));
        // Check if the ROLE exists, or create it if it doesn't
        return roleRepository.findByName("USER")
                .orElseGet(() -> roleRepository.save(new Role("USER", List.of(readPermission))));
    }

    public User addRolesAndPermissions(String userId, List<String> roleNames, List<String> permissionNames) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        // Fetch roles and permissions with validation
        List<Role> roles = roleRepository.findAllByNameIn(roleNames);
        if (roles.isEmpty()) {
            throw new RuntimeException("Some roles not found");
        }

        List<Permission> permissions = permissionRepository.findAllByNameIn(permissionNames);
        if (permissions.isEmpty()) {
            throw new RuntimeException("Some permissions not found");
        }

        // Assign roles and permissions

        user.setRoles(roles);


        return userRepository.save(user);
    }


    public void deleteAll() {
        userRepository.deleteAll();
    }
}
