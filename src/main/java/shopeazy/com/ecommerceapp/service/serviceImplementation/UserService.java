package shopeazy.com.ecommerceapp.service.serviceImplementation;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import shopeazy.com.ecommerceapp.enums.Status;
import shopeazy.com.ecommerceapp.exceptions.InvalidEmailException;
import shopeazy.com.ecommerceapp.exceptions.InvalidStatusException;
import shopeazy.com.ecommerceapp.exceptions.UserNotFoundException;
import shopeazy.com.ecommerceapp.mapper.UserMapper;
import shopeazy.com.ecommerceapp.model.document.Role;
import shopeazy.com.ecommerceapp.model.document.User;
import shopeazy.com.ecommerceapp.model.dto.request.CreateUserRequest;
import shopeazy.com.ecommerceapp.model.dto.request.UserUpdateInBulkRequest;
import shopeazy.com.ecommerceapp.model.dto.request.UserDTO;
import shopeazy.com.ecommerceapp.repository.RoleRepository;
import shopeazy.com.ecommerceapp.repository.UserRepository;

import javax.management.relation.RoleNotFoundException;
import java.security.Principal;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService implements shopeazy.com.ecommerceapp.service.contracts.UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final RedisTemplate<String, String> redisTemplate;
    private final HttpServletResponse httpServletResponse;
    private final SequenceGeneratorService sequenceGenerator;


    @Override
    public void registerUser(CreateUserRequest createUserRequest) throws RoleNotFoundException {
        long sequence = sequenceGenerator.generateSequence("userNumber");
        String userNumber = String.format("%06d", sequence);
        if (userRepository.existsByEmail(createUserRequest.getEmail())) {
            throw new InvalidEmailException("Email already exists. Try another email.");
        }
        Role userRole = getRoleByName();
        List<String> roles = List.of(userRole.getName());

        User user = new User();
        user.setUsername(createUserRequest.getUsername());
        user.setUserNumber(userNumber);
        user.setEmail(createUserRequest.getEmail());
        user.setPassword(passwordEncoder.encode(createUserRequest.getPassword().trim()));
        user.setFirstName(createUserRequest.getFirstName());
        user.setLastName(createUserRequest.getLastName());
        user.setRoles(roles);
        user.setGender(createUserRequest.getGender());
        user.setStatus(Status.ACTIVE);
        user.setCreatedAt(Instant.now());
        user.setUpdatedAt(Instant.now());
        user.setOrdersCount(0);
        user.setAddress(null);
        userRepository.save(user);

    }

    private Role getRoleByName() throws RoleNotFoundException {
        return roleRepository.findByName("USER")
                .orElseThrow(() -> new RoleNotFoundException("Role not found: " + "USER"));
    }


    @Override
    public List<UserDTO> getAll() {
        try {
            List<User> users = userRepository.findAll();

            List<User> usersExceptAdmins = users.stream()
                    .filter(user -> !user.getRoles().contains("ROLE_ADMIN"))
                    .toList();

            return usersExceptAdmins.stream()
                    .map(user -> UserMapper.mapToDTO(user, roleRepository))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("There are no users in the system. Database is empty" + e);
        }
    }

    @Override
    public User getUserByPrincipal(Principal principal) {
        if (principal == null || principal.getName() == null) {
            throw new IllegalArgumentException("Unauthenticated access- Principal is null");
        }

        return userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new UserNotFoundException(principal.getName()));
    }

    @Override
    public User getUserById(String id) {

        return userRepository.getUserById(id)
                .orElseThrow(() -> new UserNotFoundException("User with ID " + id + " doesn't exist"));
    }

    @Override
    public String deleteAllUsersExceptAdmins() {
        List<User> allUsers = userRepository.findAll();
        List<User> usersToDelete = allUsers.stream()
                .filter(user -> !user.getRoles().contains("ROLE_ADMIN"))
                .toList();
        userRepository.deleteAll(usersToDelete);
        sequenceGenerator.resetSequence("userNumber");
        return "All users successfully deleted except admin(s)";
    }


    @Override
    public void deleteById(String userId) {
        try {
            userRepository.deleteById(userId);
        } catch (EmptyResultDataAccessException e) {
            httpServletResponse.setStatus(HttpServletResponse.SC_NO_CONTENT);
        }
    }

    public void deleteUsersInBulk(UserUpdateInBulkRequest request) {
        List<String> userIds = request.getUserIDs();
        Assert.notNull(userIds, "User ID list must not be null");

        if (userIds.contains(null)) {
            throw new IllegalArgumentException("User ID list must not contain null values");
        }

        List<User> users = userRepository.findAllById(userIds);
        userRepository.deleteAll(users);
    }

    @Override
    public UserDTO updateStatus(String userId, String status) throws InvalidStatusException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User Not Found"));
        isValidStatus(status);

        try {
            user.setStatus(Status.valueOf(status));
            userRepository.save(user);
        } catch (IllegalArgumentException e) {
            throw new InvalidStatusException("Invalid status value: " + status);
        }
        return UserMapper.mapToDTO(user, roleRepository);
    }

    @Override
    public List<UserDTO> updateStatusInBulk(UserUpdateInBulkRequest request, String status) throws InvalidStatusException {
        isValidStatus(status);

        try {
            List<String> userIDs = request.getUserIDs();
            List<User> usersToUpdate = userRepository.findAllById(userIDs);
            if (usersToUpdate.size() != userIDs.size()) {
                throw new UserNotFoundException("Some users not found");
            }

            usersToUpdate.forEach(user -> user.setStatus(Status.valueOf(status)));
            userRepository.saveAll(usersToUpdate);

            return usersToUpdate.stream()
                    .map(user -> UserMapper.mapToDTO(user, roleRepository))
                    .toList();
        } catch (IllegalArgumentException e) {
            throw new InvalidStatusException("Invalid status value" + status);
        }
    }


    private static void isValidStatus(String status) throws InvalidStatusException {
        if (status == null || status.isEmpty()) {
            throw new InvalidStatusException("Status cannot be null or empty");
        }
        log.info("Status being validated: {}", status);
    }

}
