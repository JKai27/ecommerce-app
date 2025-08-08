package shopeazy.com.ecommerce_app.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import shopeazy.com.ecommerce_app.product.exception.InvalidStatusException;
import shopeazy.com.ecommerce_app.user.dto.UpdateUserProfileRequest;
import shopeazy.com.ecommerce_app.user.exception.UserNotFoundException;
import shopeazy.com.ecommerce_app.user.mapper.UserMapper;
import shopeazy.com.ecommerce_app.user.model.User;
import shopeazy.com.ecommerce_app.user.dto.CreateUserRequest;
import shopeazy.com.ecommerce_app.user.dto.UserUpdateInBulkRequest;
import shopeazy.com.ecommerce_app.admin.dto.StatusUpdateRequest;
import shopeazy.com.ecommerce_app.user.dto.UserDTO;
import shopeazy.com.ecommerce_app.common.dto.ApiResponse;
import shopeazy.com.ecommerce_app.security.repository.RoleRepository;
import shopeazy.com.ecommerce_app.user.service.UserService;

import javax.management.relation.RoleNotFoundException;
import java.security.Principal;
import java.time.Instant;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final RoleRepository roleRepository;
    private final RedisTemplate<String, String> redisTemplate;

    /*
        Get all Users with Role "USER" (no admins)
     */
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping
    public ResponseEntity<ApiResponse<List<UserDTO>>> getAll() {
        List<UserDTO> users = userService.getAll();
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Users retrieved successfully", users, Instant.now())
        );
    }

    /*
        Get a User by its ID
     */
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserDTO>> getUserById(@PathVariable String userId) {
        User user = userService.getUserById(userId);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ApiResponse<>(false, "User not found", null, Instant.now())
            );
        }
        return ResponseEntity.ok(
                new ApiResponse<>(true, "User retrieved successfully", UserMapper.mapToDTO(user, roleRepository), Instant.now())
        );
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @GetMapping("/me/profile")
    public ResponseEntity<ApiResponse<UserDTO>> getOwnProfile(Principal principal) {
        User user = userService.getUserByPrincipal(principal);
        User currentUser = userService.getUserById(user.getId());
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Your Profile", UserMapper.mapToDTO(currentUser, roleRepository), Instant.now())

        );
    }


    @PreAuthorize("hasAuthority('ROLE_USER')")
    @PatchMapping("/me/profile")
    public ResponseEntity<ApiResponse<UserDTO>> updateOwnProfile(@Valid @RequestBody UpdateUserProfileRequest request, Principal principal) {
        User user = userService.getUserByPrincipal(principal);
        UserDTO updatedUserProfile = userService.updateOwnProfile(request, user.getId());
        return ResponseEntity.ok(
                new ApiResponse<>(true, "You have successfully updated your profile.", updatedUserProfile, Instant.now())
        );
    }

    /*
        Create a User with Redis generated User-Id. While in the DB, the ID has MongoDB-ID
     */
    @PostMapping
    public ResponseEntity<ApiResponse<UserDTO>> registerUser(@Valid @RequestBody CreateUserRequest user) throws RoleNotFoundException {
        UserDTO registeredUser = userService.registerUser(user);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "User created successfully", registeredUser, Instant.now())
        );
    }

    /*
        Update Status, needs @var userId. Authorized: only admin
     */
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PatchMapping("/{userId}/status")
    public ResponseEntity<ApiResponse<UserDTO>> updateStatus(@PathVariable String userId,
                                                             @RequestBody StatusUpdateRequest request)
            throws InvalidStatusException {
        String status = request.getStatus().toUpperCase();
        UserDTO updatedUser = userService.updateStatus(userId, status);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "User status updated successfully to " + status, updatedUser, Instant.now())
        );
    }

    /*
        Delete a User with its ID. This action can only be performed with Authority 'ADMIN'
     */
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @DeleteMapping("/{userId}/delete")
    public ResponseEntity<ApiResponse<String>> deleteUserById(@PathVariable String userId) {
        String email = userService.getUserById(userId).getEmail();
        userService.deleteById(userId);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "User with the userId (" + userId + ") deleted successfully", email, Instant.now())
        );
    }



    /*
        -------------------------------- Bulk endpoints -----------------------------
     */

    /*
     update status at the same time for multiple users
  */
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PatchMapping("/bulkStatus-update/{status}")
    public ResponseEntity<ApiResponse<List<UserDTO>>> updateStatusInBulk(@Valid @RequestBody UserUpdateInBulkRequest request,
                                                                         @PathVariable String status) throws InvalidStatusException {
        List<UserDTO> userDTOList = userService.updateStatusInBulk(request, status);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "User statuses updated successfully", userDTOList, Instant.now())
        );
    }


    /*
        Delete bulk users with List of userIds
     */

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @DeleteMapping("/bulk-delete")
    public ResponseEntity<ApiResponse<String>> deleteUsersInBulk(@Valid @RequestBody UserUpdateInBulkRequest request) {
        try {
            userService.deleteUsersInBulk(request);
            return ResponseEntity.ok(
                    new ApiResponse<>(true, "Users deleted successfully", null, Instant.now())
            );
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ApiResponse<>(false, "Some users not found", null, Instant.now())
            );
        }
    }

    /*
        Endpoint to delete all users except admins.
        This action can only be performed with Authority 'ADMIN'
     */
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @DeleteMapping("/deleteAll")
    public ResponseEntity<ApiResponse<String>> deleteAllUsers() {
        String responseString = userService.deleteAllUsersExceptAdmins();
        return ResponseEntity.ok(
                new ApiResponse<>(true, responseString, null, Instant.now())
        );
    }
}

