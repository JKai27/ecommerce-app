package shopeazy.com.ecommerceapp.service;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import shopeazy.com.ecommerceapp.model.User;
import shopeazy.com.ecommerceapp.model.dto.JwtResponse;
import shopeazy.com.ecommerceapp.model.dto.LoginRequest;
import shopeazy.com.ecommerceapp.repository.UserRepository;
import shopeazy.com.ecommerceapp.security.JwtUtil;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public AuthService(JwtUtil jwtUtil, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Authenticates the user and generates a JWT token.
     *
     * @param loginRequest The login credentials (email and password).
     * @return A JwtResponse containing the JWT token.
     */
    public String authenticateUser(LoginRequest loginRequest) {
        // Check if user exists in the database by email
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + loginRequest.getEmail()));
        System.out.println("User found: " + user);

        // Check if the password matches
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid email or password");
        }
        System.out.println("Generating JWT token for user: " + user.getEmail());

        // Generate JWT token for the authenticated user
        String jwtToken = jwtUtil.generateJwtToken(user);
        System.out.println("Generated JWT token: " + jwtToken);

        // Return JWT response with the token
        return jwtToken;
    }
}
