package shopeazy.com.ecommerceapp.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import shopeazy.com.ecommerceapp.model.User;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class JwtUtil {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

    @Value("${jwt.secret}")
    private String jwtSecretKey;

    @Value("${jwt.expirationInMinutes}")
    private long expirationTime; // Expiration time in minutes

    private SecretKey secretKey;
    private JwtParser jwtParser;

    @PostConstruct
    public void init() {
        // Initialize the secret key using HMAC
        this.secretKey = Keys.hmacShaKeyFor(jwtSecretKey.getBytes(StandardCharsets.UTF_8));
        this.jwtParser = Jwts.parserBuilder().setSigningKey(secretKey).build();
    }

    /**
     * Generate JWT token for a user.
     *
     * @param user The user for whom the token is generated.
     * @return JWT token as a String.
     */
    public String generateJwtToken(User user) {

        String token = Jwts.builder()
                .setSubject(user.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + expirationTime * 60 * 1000)) // expiration in minutes
                .claim("roles", user.getRoles().stream().map(role -> "ROLE_" + role.getName()).collect(Collectors.toList())) // Add role prefix
                .signWith(secretKey)
                .compact();

        // Log the generated token (only for debugging)
        System.out.println("Generated JWT Token: " + token);

        return token;
    }

    /**
     * Get the username (subject) from the JWT token.
     *
     * @param token The JWT token.
     * @return The username (subject) from the JWT.- here user's email
     */
    public String getSubjectFromJwtToken(String token) {
        try {
            return jwtParser.parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (JwtException e) {
            logger.error("Failed to parse JWT token:- {}", e.getMessage());
            throw new IllegalArgumentException("Invalid JWT token.");
        }
    }

    /**
     * Validate the JWT token.
     *
     * @param authToken The JWT token to validate.
     * @return True if valid, false otherwise.
     */
    public boolean validateJwtToken(String authToken, User user) {
        try {
            String username = getSubjectFromJwtToken(authToken);
            return username.equals(user.getEmail()) && (!isTokenExpired(authToken));
        } catch (Exception e) {
            logger.error("Token validation error: {}", e.getMessage());
        }
        return false;
    }

    private boolean isTokenExpired(String token) {
        return extractExpirationDateFromToken(token).before(new Date());
    }

    private Date extractExpirationDateFromToken(String token) {
        return extractClaimFromToken(token, Claims::getExpiration);
    }


    public List<String> extractRolesFromToken(String token) {
        String rolesString = extractClaimFromToken(token, claims -> claims.get("roles", String.class));
        return Arrays.asList(rolesString.split(","));
    }

    // Extract JWT token from cookies
    public static String getTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("authToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    private <T> T extractClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            logger.error("Token has expired: {}", e.getMessage());
            throw e;
        } catch (UnsupportedJwtException e) {
            logger.error("Unsupported JWT: {}", e.getMessage());
            throw e;
        } catch (MalformedJwtException e) {
            logger.error("Malformed JWT: {}", e.getMessage());
            throw e;
        } catch (SecurityException e) {
            logger.error("Invalid signature: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Token parsing error: {}", e.getMessage());
            throw e;
        }
    }

}