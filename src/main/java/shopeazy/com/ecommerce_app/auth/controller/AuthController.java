package shopeazy.com.ecommerce_app.auth.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shopeazy.com.ecommerce_app.auth.dto.LoginRequest;
import shopeazy.com.ecommerce_app.auth.dto.LoginResponse;
import shopeazy.com.ecommerce_app.auth.service.AuthService;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        try {
            LoginResponse loginResponse = authService.login(loginRequest, response);
            return ResponseEntity.ok(loginResponse);
        } catch (IllegalArgumentException e) {
            log.error("Login failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new LoginResponse(null, null, null, null, null, false, null));
        } catch (Exception e) {
            log.error("Unexpected error during login", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new LoginResponse(null, null, null, null, null, false, null));
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refreshToken(@CookieValue(name = "refresh_token", required = true) String refreshToken) {
        try {
            HttpHeaders responseHeaders = new HttpHeaders();
            LoginResponse loginResponse = authService.refresh(refreshToken, responseHeaders);
            return ResponseEntity.ok().headers(responseHeaders).body(loginResponse);
        } catch (Exception e) {
            log.error("Token refresh failed", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new LoginResponse(null, null, null, null, null, false, null));
        }
    }

    @DeleteMapping("/logout")
    public ResponseEntity<String> logout(
            @CookieValue(name = "access_token", required = false) String accessToken,
            @CookieValue(name = "refresh_token", required = false) String refreshToken,
            HttpServletResponse response, HttpServletRequest request) {
        try {
            // Ensure that the access and refresh tokens are provided
            if (accessToken == null || refreshToken == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not logged in");
            }

            String username = authService.logout(accessToken, refreshToken, response, request);

            if (username != null && !username.startsWith("Logging out for user")) {
                return ResponseEntity.ok("The user with email -- " + username + " -- logged out successfully");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(username);
            }

        } catch (Exception e) {
            log.error("Error while logging out", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error during logout: " + e.getMessage());
        }
    }
}