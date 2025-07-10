package shopeazy.com.ecommerce_app.service.contracts;

import shopeazy.com.ecommerce_app.model.document.Permission;
import shopeazy.com.ecommerce_app.model.document.Role;
import shopeazy.com.ecommerce_app.model.document.User;
import java.util.List;

public interface RoleAssignmentService {
    Role assignRoleToUser(User user, String roleName);
    Role createRoleWithPermissions(String roleName, List<Permission> permissionNames);
    Role getOrCreateDefaultRole();
}
