package shopeazy.com.ecommerce_app.service.contracts;

import shopeazy.com.ecommerce_app.exceptions.InvalidStatusException;
import shopeazy.com.ecommerce_app.model.document.User;
import shopeazy.com.ecommerce_app.model.dto.request.CreateUserRequest;
import shopeazy.com.ecommerce_app.model.dto.request.UserUpdateInBulkRequest;
import shopeazy.com.ecommerce_app.model.dto.request.UserDTO;

import javax.management.relation.RoleNotFoundException;
import java.security.Principal;
import java.util.List;

public interface UserService {

    UserDTO registerUser(CreateUserRequest request) throws RoleNotFoundException;
    List<UserDTO> getAll();

    User getUserByPrincipal(Principal principal);
    User getUserById(String id);
    String deleteAllUsersExceptAdmins();
    void deleteById(String userId);
    void deleteUsersInBulk(UserUpdateInBulkRequest request);
    UserDTO updateStatus(String userId, String status) throws InvalidStatusException;
    List<UserDTO> updateStatusInBulk(UserUpdateInBulkRequest request, String status) throws InvalidStatusException;
}
