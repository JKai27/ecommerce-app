package shopeazy.com.ecommerce_app.security.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RoleInitializer {

    private final RoleAssignmentService roleAssignmentService;

    @PostConstruct
    public void initDefaultRoles() {
        roleAssignmentService.getOrCreateDefaultRole();
    }
}