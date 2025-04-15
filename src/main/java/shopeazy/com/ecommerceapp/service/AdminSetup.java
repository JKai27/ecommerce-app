package shopeazy.com.ecommerceapp.service;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import shopeazy.com.ecommerceapp.model.Permission;
import shopeazy.com.ecommerceapp.model.Role;
import shopeazy.com.ecommerceapp.model.Status;
import shopeazy.com.ecommerceapp.model.User;
import shopeazy.com.ecommerceapp.repository.PermissionRepository;
import shopeazy.com.ecommerceapp.repository.RoleRepository;
import shopeazy.com.ecommerceapp.repository.UserRepository;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

@Component
public class AdminSetup implements CommandLineRunner {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public AdminSetup(PermissionRepository permissionRepository, UserRepository userRepository, RoleRepository roleRepository, BCryptPasswordEncoder passwordEncoder) {
        this.permissionRepository = permissionRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // Check if the admin user already exists by email or username
        if (!userRepository.existsByEmail("admin@admin.com")) {
            // If the admin doesn't exist, create the permissions and roles
            Permission readPermission = permissionRepository.findByName("READ")
                    .orElseGet(() -> permissionRepository.save(new Permission("READ")));
            Permission writePermission = permissionRepository.findByName("WRITE")
                    .orElseGet(() -> permissionRepository.save(new Permission("WRITE")));
            Permission deletePermission = permissionRepository.findByName("DELETE")
                    .orElseGet(() -> permissionRepository.save(new Permission("DELETE")));
            Permission createPermission = permissionRepository.findByName("CREATE")
                    .orElseGet(() -> permissionRepository.save(new Permission("CREATE")));

            User adminUser = getUser(readPermission, writePermission, deletePermission, createPermission);
            userRepository.save(adminUser);  // Save admin user

            System.out.println("Admin user created successfully.");
        } else {
            System.out.println("Admin user already exists.");
        }
    }

    private User getUser(Permission readPermission, Permission writePermission, Permission deletePermission, Permission createPermission) {

        // Check if the admin role already exists
        Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                .orElseGet(() -> new Role("ROLE_ADMIN", Arrays.asList(readPermission, writePermission, deletePermission, createPermission)));

        roleRepository.save(adminRole);
        User adminUser = new User();
        adminUser.setFirstName("Admin");
        adminUser.setUsername("admin");
        adminUser.setLastName("User");
        adminUser.setEmail("admin@admin.com");
        adminUser.setPassword(passwordEncoder.encode("adminPassword123!"));  // Password is now hashed
        adminUser.setRoles(List.of(adminRole));
        adminUser.setStatus(Status.ACTIVE);
        adminUser.setCreatedAt(Instant.now());
        return adminUser;
    }
}