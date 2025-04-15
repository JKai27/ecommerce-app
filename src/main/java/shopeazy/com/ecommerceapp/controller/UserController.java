package shopeazy.com.ecommerceapp.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shopeazy.com.ecommerceapp.model.User;
import shopeazy.com.ecommerceapp.model.dto.CreateUserRequest;
import shopeazy.com.ecommerceapp.repository.UserRepository;
import shopeazy.com.ecommerceapp.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {
    public UserService userService;
    public UserRepository userRepository;

    public UserController(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @PostMapping("/create")
    public ResponseEntity<User> createUser(@Valid @RequestBody CreateUserRequest user) {
        User newUser = userService.create(user);
        return ResponseEntity.ok(newUser);
    }


    @GetMapping("/getAll")
    public ResponseEntity<List<User>> findAll() {
        List<User> users = userRepository.findAll();
        return ResponseEntity.ok(users);
    }

    @DeleteMapping("/deleteAll")
    public ResponseEntity<String> deleteAllUsers() {
        userService.deleteAll();
        return ResponseEntity.ok("Deleted all users");
    }
}
