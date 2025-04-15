package shopeazy.com.ecommerceapp.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import shopeazy.com.ecommerceapp.model.dto.JwtResponse;
import shopeazy.com.ecommerceapp.model.dto.LoginRequest;
import shopeazy.com.ecommerceapp.service.AuthService;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            String response = authService.authenticateUser(loginRequest);
            ResponseCookie cookie = ResponseCookie.from("authToken", response)
                    .httpOnly(true)
                    .secure(false) // will work for http <-- only for local dev, by production (true) works for https
                    .path("/")
                    .maxAge(3600)
                    .sameSite("Strict")
                    .build();
            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE,cookie.toString())
                    .body(new JwtResponse(response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }

    }
}
