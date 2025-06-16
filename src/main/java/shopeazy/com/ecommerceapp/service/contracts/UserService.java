package shopeazy.com.ecommerceapp.service.contracts;

import shopeazy.com.ecommerceapp.exceptions.InvalidStatusException;
import shopeazy.com.ecommerceapp.model.document.User;
import shopeazy.com.ecommerceapp.model.dto.request.CreateUserRequest;
import shopeazy.com.ecommerceapp.model.dto.request.UserUpdateInBulkRequest;
import shopeazy.com.ecommerceapp.model.dto.request.UserDTO;

import javax.management.relation.RoleNotFoundException;
import java.security.Principal;
import java.util.List;

public interface UserService {

    void create(CreateUserRequest request) throws RoleNotFoundException;
    List<UserDTO> getAll();

    User getUserByPrincipal(Principal principal);
    User getUserById(String id);
    String deleteAllUsersExceptAdmins();
    void deleteById(String userId);
    void deleteUsersInBulk(UserUpdateInBulkRequest request);
    UserDTO updateStatus(String userId, String status) throws InvalidStatusException;
    List<UserDTO> updateStatusInBulk(UserUpdateInBulkRequest request, String status) throws InvalidStatusException;
}
