package shopeazy.com.ecommerce_app.auth.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import shopeazy.com.ecommerce_app.common.exception.AppException;
import shopeazy.com.ecommerce_app.security.repository.RoleRepository;
import shopeazy.com.ecommerce_app.user.dto.UserDTO;
import shopeazy.com.ecommerce_app.user.mapper.UserMapper;
import shopeazy.com.ecommerce_app.user.model.User;
import shopeazy.com.ecommerce_app.auth.dto.LoginRequest;
import shopeazy.com.ecommerce_app.auth.dto.LoginResponse;
import shopeazy.com.ecommerce_app.user.repository.UserRepository;
import shopeazy.com.ecommerce_app.security.jwt.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import shopeazy.com.ecommerce_app.common.util.CookieUtil;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final RoleRepository roleRepository;
    @Value("${jwt.expiration}")
    private long jwtExpirationMs;
    @Value("${jwt.refresh-expiration}")
    private long refreshTokenExpirationMs;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final Logger logger = LoggerFactory.getLogger(AuthService.class);
    private final BCryptPasswordEncoder passwordEncoder;
    private final CookieUtil cookieUtil;


    public UserDTO login(LoginRequest loginRequest, HttpServletResponse response) {
        // Step 1: Validate User Credentials
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("User with email " + loginRequest.getEmail() + " not found"));

        // Step 2: Check if the password matches
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid credentials");
        }

        // Step 3: Create an Authentication object using roles as GrantedAuthority
        List<SimpleGrantedAuthority> authorities = user.getRoles().stream()
                .map(SimpleGrantedAuthority::new)
                .toList();

        Authentication authentication = new UsernamePasswordAuthenticationToken(user.getEmail(), null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication); // Store it in the security context

        // Step 4: Generate JWT Tokens (Access and Refresh)
        String accessToken = jwtService.generateAccessToken(authentication);
        String refreshToken = jwtService.generateRefreshToken(authentication);

        // Step 5: Set JWT tokens in cookies
        setJwtCookie(response, accessToken, refreshToken);

        // Step 6: Return LoginResponse
        return UserMapper.mapToDTO(user, roleRepository);
    }

    public UserDTO refresh(String refreshToken, HttpHeaders responseHeaders) {
        boolean refreshTokenValid = jwtService.validateToken(refreshToken);

        if (!refreshTokenValid)
            throw new AppException(HttpStatus.BAD_REQUEST, "Refresh token is invalid");

        String username = jwtService.getUsernameFromToken(refreshToken);
        User user = userRepository.findByEmail(username).orElseThrow(
                () -> new UsernameNotFoundException("User not found")
        );

        String newAccessToken = jwtService.generateTokenWithExpiration(
                new UsernamePasswordAuthenticationToken(username, null),
                jwtExpirationMs
        );

        addAccessTokenCookie(responseHeaders, newAccessToken);

        return UserMapper.mapToDTO(user, roleRepository);
    }

    private void setJwtCookie(HttpServletResponse response, String accessToken, String refreshToken) {
        // Clear old access_token
        ResponseCookie clearAccessToken = ResponseCookie.from("access_token", "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .sameSite("Strict")
                .build();

        // Clear old refresh_token
        ResponseCookie clearRefreshToken = ResponseCookie.from("refresh_token", "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .sameSite("Strict")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, clearAccessToken.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, clearRefreshToken.toString());

        // set a new access token
        ResponseCookie accessTokenCookie = ResponseCookie.from("access_token", accessToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge((int) jwtExpirationMs)
                .sameSite("Strict")
                .build();

        ResponseCookie refreshTokenCookie = ResponseCookie.from("refresh_token", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge((int) refreshTokenExpirationMs)
                .sameSite("Strict")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());
    }

    public String logout(String accessToken, String refreshToken, HttpServletResponse response, HttpServletRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        log.info("{} set to invalid", accessToken);
        log.info("{} set to invalid", refreshToken);

        String username = null;

        if (auth != null && auth.isAuthenticated()) {
            username = auth.getName();
            // Perform user-specific logout logic, if necessary (e.g., check if email matches)
            if (userRepository.findByEmail(username).isPresent()) {
                // Clear the authentication context
                SecurityContextHolder.clearContext();
                logger.info("User with email {} logged out successfully.", username);

                request.getSession().invalidate();

                // Clear the cookies (access_token and refresh_token)
                clearJwtCookies(response);
                return username;
            } else {
                logger.warn("Logout failed for user with email {}. No matching user found.", username);
            }
        } else {
            logger.warn("No authentication found for user with email {}.", username);
        }
        return "Logging out for user with email " + username + " failed";
    }

    private void clearJwtCookies(HttpServletResponse response) {
        // Clear Access Token Cookie using ResponseCookie
        ResponseCookie accessTokenCookie = ResponseCookie.from("access_token", "")
                .httpOnly(true)
                .secure(false)  // Set to true for production (use HTTPS)
                .path("/")
                .maxAge(0)  // Expire the cookie
                .sameSite("Strict")
                .build();

        // Clear Refresh Token Cookie using ResponseCookie
        ResponseCookie refreshTokenCookie = ResponseCookie.from("refresh_token", "")
                .httpOnly(true)
                .secure(false) // Set to true for production (use HTTPS)
                .path("/")
                .maxAge(0)
                .sameSite("Strict")
                .build();

        // Add cookies to response headers using ResponseCookie's toString method
        response.addHeader(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());
    }

    private void addAccessTokenCookie(HttpHeaders httpHeaders, String token) {
        httpHeaders.add(HttpHeaders.SET_COOKIE, cookieUtil.createAccessTokenCookie(token, jwtExpirationMs).toString());
    }

    // not used yet
    private void addRefreshTokenCookie(HttpHeaders httpHeaders, String token) {
        httpHeaders.add(HttpHeaders.SET_COOKIE, cookieUtil.createRefreshTokenCookie(token, refreshTokenExpirationMs).toString());
    }
}