package shopeazy.com.ecommerce_app.admin.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import shopeazy.com.ecommerce_app.common.enums.Gender;
import shopeazy.com.ecommerce_app.common.enums.Status;
import shopeazy.com.ecommerce_app.security.model.Permission;
import shopeazy.com.ecommerce_app.security.model.Role;
import shopeazy.com.ecommerce_app.user.model.User;
import shopeazy.com.ecommerce_app.user.repository.UserRepository;
import shopeazy.com.ecommerce_app.security.service.RoleAssignmentService;

import java.time.Instant;
import java.util.List;
@Slf4j
@Component
@RequiredArgsConstructor
public class AdminSetup implements CommandLineRunner {
    private final UserRepository userRepository;
    private final RoleAssignmentService roleAssignmentService;
    private final BCryptPasswordEncoder passwordEncoder;


    @Override
    public void run(String... args) throws Exception {
        if (!userRepository.existsByEmail("admin@admin.com")) {
            List<Permission> adminPermissions = List.of(
                    new Permission("READ"),
                    new Permission("WRITE"),
                    new Permission("DELETE"),
                    new Permission("CREATE")
            );

            Role adminRole = roleAssignmentService.createRoleWithPermissions(
                    "ROLE_ADMIN", adminPermissions
            );
            User adminUser = getUser(adminRole);
            userRepository.save(adminUser);
            log.info("Admin user created successfully.");
        } else {
            log.info("Admin user already exists.");
        }
    }

    private User getUser(Role adminRole) {
        User adminUser = new User();
        adminUser.setFirstName("Admin");
        adminUser.setLastName("User");
        adminUser.setUsername("admin");
        adminUser.setEmail("admin@admin.com");
        adminUser.setGender(Gender.FEMALE);
        adminUser.setPassword(passwordEncoder.encode("adminPassword123!"));
        // Set roles as List of role names
        adminUser.setRoles(List.of(adminRole.getName()));
        adminUser.setStatus(Status.ACTIVE);
        adminUser.setCreatedAt(Instant.now());
        return adminUser;
    }
}