package shopeazy.com.ecommerceapp.service.contracts;

import shopeazy.com.ecommerceapp.model.document.Permission;
import shopeazy.com.ecommerceapp.model.document.Role;
import shopeazy.com.ecommerceapp.model.document.User;
import java.util.List;

public interface RoleAssignmentService {
    Role assignRoleToUser(User user, String roleName);
    Role createRoleWithPermissions(String roleName, List<Permission> permissionNames);
    Role getOrCreateDefaultRole();
}
