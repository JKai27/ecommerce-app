package shopeazy.com.ecommerce_app.security.service;

import shopeazy.com.ecommerce_app.security.model.Permission;
import shopeazy.com.ecommerce_app.security.model.Role;
import shopeazy.com.ecommerce_app.user.model.User;
import java.util.List;

public interface RoleAssignmentService {
    Role assignRoleToUser(User user, String roleName);
    Role createRoleWithPermissions(String roleName, List<Permission> permissionNames);
    Role getOrCreateDefaultRole();
}
