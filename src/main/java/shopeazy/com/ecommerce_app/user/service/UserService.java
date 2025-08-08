package shopeazy.com.ecommerce_app.user.service;

import shopeazy.com.ecommerce_app.product.exception.InvalidStatusException;
import shopeazy.com.ecommerce_app.user.dto.UpdateUserProfileRequest;
import shopeazy.com.ecommerce_app.user.model.User;
import shopeazy.com.ecommerce_app.user.dto.CreateUserRequest;
import shopeazy.com.ecommerce_app.user.dto.UserUpdateInBulkRequest;
import shopeazy.com.ecommerce_app.user.dto.UserDTO;

import javax.management.relation.RoleNotFoundException;
import java.security.Principal;
import java.util.List;

public interface UserService {

    UserDTO registerUser(CreateUserRequest request) throws RoleNotFoundException;
    List<UserDTO> getAll();

    User getUserByPrincipal(Principal principal);
    User getUserById(String id);
    String deleteAllUsersExceptAdmins();

    UserDTO updateOwnProfile(UpdateUserProfileRequest request, String userId);

    void deleteById(String userId);
    void deleteUsersInBulk(UserUpdateInBulkRequest request);
    UserDTO updateStatus(String userId, String status) throws InvalidStatusException;
    List<UserDTO> updateStatusInBulk(UserUpdateInBulkRequest request, String status) throws InvalidStatusException;
}
