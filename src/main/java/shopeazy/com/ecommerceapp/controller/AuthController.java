package shopeazy.com.ecommerceapp.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shopeazy.com.ecommerceapp.model.dto.request.LoginRequest;
import shopeazy.com.ecommerceapp.model.dto.response.LoginResponse;
import shopeazy.com.ecommerceapp.service.serviceImplementation.AuthService;

@Slf4j
@RestController
@RequestMapping("api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        return authService.login(loginRequest, response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refreshToken(@CookieValue(name = "refresh_token", required = true) String refreshToken) {
        return authService.refresh(refreshToken);
    }

    @DeleteMapping("/logout")
    public ResponseEntity<?> logout(
            @CookieValue(name = "access_token", required = false) String accessToken,
            @CookieValue(name = "refresh_token", required = false) String refreshToken,
            HttpServletResponse response, HttpServletRequest request) {
        try {
            // Ensure that the access and refresh tokens are provided
            if (accessToken == null || refreshToken == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not logged in");
            }

            String username = authService.logout(accessToken, refreshToken, response, request);

            return ResponseEntity.ok("The user with email -- " + username + " -- logged out successfully");

        } catch (Exception e) {
            log.error("Error while logging out", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error during logout: " + e.getMessage());
        }
    }


}
