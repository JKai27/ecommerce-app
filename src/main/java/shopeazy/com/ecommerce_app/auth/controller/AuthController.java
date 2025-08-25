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
import shopeazy.com.ecommerce_app.auth.service.AuthService;
import shopeazy.com.ecommerce_app.user.dto.UserDTO;
import shopeazy.com.ecommerce_app.common.dto.ApiResponse;

import java.time.Instant;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<UserDTO>> login(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        UserDTO userDTO = authService.login(loginRequest, response);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Login successful", userDTO, Instant.now())
        );
    }
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<UserDTO>> refreshToken(
            @CookieValue(name = "refresh_token", required = true) String refreshToken) {
        try {
            HttpHeaders responseHeaders = new HttpHeaders();
            UserDTO userDTO = authService.refresh(refreshToken, responseHeaders);
            return ResponseEntity.ok()
                    .headers(responseHeaders)
                    .body(new ApiResponse<>(true, "Token refreshed successfully", userDTO, Instant.now()));
        } catch (Exception e) {
            log.error("Token refresh failed", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    new ApiResponse<>(false, "Token refresh failed", null, Instant.now())
            );
        }
    }


    @DeleteMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(
            @CookieValue(name = "access_token", required = false) String accessToken,
            @CookieValue(name = "refresh_token", required = false) String refreshToken,
            HttpServletResponse response, HttpServletRequest request) {
        try {
            // Ensure that the access and refresh tokens are provided
            if (accessToken == null || refreshToken == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                        new ApiResponse<>(false, "User not logged in", null, Instant.now())
                );
            }

            String username = authService.logout(accessToken, refreshToken, response, request);

            if (username != null && !username.startsWith("Logging out for user")) {
                return ResponseEntity.ok(
                        new ApiResponse<>(true, "The user with email -- " + username + " -- logged out successfully", username, Instant.now())
                );
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                        new ApiResponse<>(false, username, null, Instant.now())
                );
            }

        } catch (Exception e) {
            log.error("Error while logging out", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Error during logout: " + e.getMessage(), null, Instant.now()));
        }
    }
}