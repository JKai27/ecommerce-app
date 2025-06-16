package shopeazy.com.ecommerceapp.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import shopeazy.com.ecommerceapp.exceptions.InvalidStatusException;
import shopeazy.com.ecommerceapp.exceptions.UserNotFoundException;
import shopeazy.com.ecommerceapp.mapper.UserMapper;
import shopeazy.com.ecommerceapp.model.document.User;
import shopeazy.com.ecommerceapp.model.dto.request.CreateUserRequest;
import shopeazy.com.ecommerceapp.model.dto.request.UserUpdateInBulkRequest;
import shopeazy.com.ecommerceapp.model.dto.request.StatusUpdateRequest;
import shopeazy.com.ecommerceapp.model.dto.request.UserDTO;
import shopeazy.com.ecommerceapp.repository.RoleRepository;
import shopeazy.com.ecommerceapp.service.contracts.UserService;

import javax.management.relation.RoleNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    /*
        ToDO: Call Service Methods through UserService contract.
     */
    private final UserService userService;
    private final RoleRepository roleRepository;
    private final RedisTemplate<String, String> redisTemplate;

    /*
        Get all Users with Role "USER" (no admins)
     */
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/getAll")
    public ResponseEntity<?> getAll() {
        Map<String, Object> response = new HashMap<>();
        List<UserDTO> users = userService.getAll();
        response.put("users", users);
        return ResponseEntity.ok(response);
    }

    /*
        Get a User by its ID
     */
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/{userId}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable String userId) {
        User user = userService.getUserById(userId);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(UserMapper.mapToDTO(user, roleRepository));
    }

    /*
        Create a User with Redis generated User-Id. While in the DB, the ID has MongoDB-ID
     */
    @PostMapping("/create")
    public ResponseEntity<?> createUser(@Valid @RequestBody CreateUserRequest user) throws RoleNotFoundException {
        Map<String, String> response = new HashMap<>();
        response.put("message", "User created successfully");
        userService.create(user);
        return ResponseEntity.ok(response);
    }

    /*
        Update Status, needs @var userId. Authorized: only admin
     */
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PatchMapping("/{userId}/status")
    public ResponseEntity<Map<String, Object>> updateStatus(@PathVariable String userId,
                                                            @RequestBody StatusUpdateRequest request)
            throws InvalidStatusException {
        String status = request.getStatus().toUpperCase();
        UserDTO updatedUser = userService.updateStatus(userId, status);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "User status updated successfully to " + status);
        response.put("updatedUser", updatedUser);
        return ResponseEntity.ok(response);
    }

    /*
        Delete a User with its ID. This action can only be performed with Authority 'ADMIN'
     */
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @DeleteMapping("/{userId}/delete")
    public ResponseEntity<?> deleteUserById(@PathVariable String userId) {
        Map<String, Object> response = new HashMap<>();
        String email = userService.getUserById(userId).getEmail();

        userService.deleteById(userId);
        response.put("deleted users email", email);
        response.put("message", "User with the userId (" + userId + ") deleted successfully");

        return ResponseEntity.ok(response);
    }



    /*
        -------------------------------- Bulk endpoints -----------------------------
     */

    /*
     update status at the same time for multiple users
  */
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PatchMapping("/bulkStatus-update/{status}")
    public ResponseEntity<?> updateStatusInBulk(@Valid @RequestBody UserUpdateInBulkRequest request,
                                                @PathVariable String status) throws InvalidStatusException {
        Map<String, Object> response = new HashMap<>();
        List<UserDTO> userDTOList = userService.updateStatusInBulk(request, status);
        response.put("updated-users", userDTOList);
        return ResponseEntity.ok(response);
    }


    /*
        Delete bulk users with List of userIds
     */

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @DeleteMapping("/bulk-delete")
    public ResponseEntity<?> deleteUsersInBulk(@Valid @RequestBody UserUpdateInBulkRequest request) {
        try {
            userService.deleteUsersInBulk(request);
            return ResponseEntity.noContent().build();
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Some users not found");
        }
    }

    /*
        Endpoint to delete all users except admins.
        This action can only be performed with Authority 'ADMIN'
     */
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @DeleteMapping("/deleteAll")
    public ResponseEntity<?> deleteAllUsers() {
        Map<String, Object> response = new HashMap<>();
        String responseString = userService.deleteAllUsersExceptAdmins();
        response.put("message", responseString);
        return ResponseEntity.ok(response);
    }
}

