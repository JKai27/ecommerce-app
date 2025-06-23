package shopeazy.com.ecommerceapp.service.serviceImplementation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import shopeazy.com.ecommerceapp.enums.RoleType;
import shopeazy.com.ecommerceapp.exceptions.ResourceNotFoundException;
import shopeazy.com.ecommerceapp.model.document.Permission;
import shopeazy.com.ecommerceapp.model.document.Role;
import shopeazy.com.ecommerceapp.model.document.User;
import shopeazy.com.ecommerceapp.repository.PermissionRepository;
import shopeazy.com.ecommerceapp.repository.RoleRepository;
import shopeazy.com.ecommerceapp.repository.UserRepository;
import shopeazy.com.ecommerceapp.service.contracts.RoleAssignmentService;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoleAssignmentServiceImpl implements RoleAssignmentService {
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final UserRepository userRepository;

    @Override
    public Role assignRoleToUser(User user, String roleName) {
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new ResourceNotFoundException("Role does not exist: " + roleName));

        List<String> updatedRoles = user.getRoles();
        if (updatedRoles == null) {
            updatedRoles = new ArrayList<>();
        }

        if (!updatedRoles.contains(roleName)) {
            updatedRoles.add(roleName);
            user.setRoles(updatedRoles);
            userRepository.save(user);
        }

        return role;
    }




    @Override
    public Role createRoleWithPermissions(String roleName, List<Permission> permissionSet) {
        List<Permission> permissions = permissionSet.stream()
                .map(permission -> permissionRepository.findByName(permission.getName())
                        .orElseGet(() -> permissionRepository.save(new Permission(permission.getName()))))
                .toList();

        return roleRepository.findByName(roleName)
                .orElseGet(() -> roleRepository.save(new Role(roleName, permissions)));
    }

    @Override
    public Role getOrCreateDefaultRole() {
        Permission readPermission = permissionRepository.findByName("READ")
                .orElseGet(() -> permissionRepository.save(new Permission("READ")));
        return roleRepository.findByName(RoleType.ROLE_USER.name())
                .orElseGet(() -> roleRepository.save(new Role(RoleType.ROLE_USER.name(), List.of(readPermission))));
    }
}
