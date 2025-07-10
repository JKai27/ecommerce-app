package shopeazy.com.ecommerce_app.jwt;


import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import shopeazy.com.ecommerce_app.model.document.User;
import shopeazy.com.ecommerce_app.repository.UserRepository;
import shopeazy.com.ecommerce_app.userDetails.CustomUserDetailsService;


import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.function.Function;


@Service
@Slf4j
@RequiredArgsConstructor
public class JwtService {

    @Value("${jwt.secret}")
    private String jwtSecretKey;

    @Value("${jwt.expiration}")
    private long jwtExpirationMs;
    @Value("${jwt.refresh-expiration}")
    private long refreshTokenExpirationMs;
    private final UserRepository userRepository;

    private Key getSignInKey() {
        return Keys.hmacShaKeyFor(jwtSecretKey.getBytes());
    }

    // Helper method to generate a token (both Access and Refresh)
    public String generateTokenWithExpiration(Authentication authentication, long expirationTimeMs) {
        String usersEmail = (String) authentication.getPrincipal(); // Extracting the email directly from authentication
        User user = userRepository.findByEmail(usersEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        List<String> roles = user.getRoles();  // Assuming the roles are stored as Strings
        log.info("User roles before generating token: {}", roles);
        // Setting JWT Claims
        Claims claims = Jwts.claims().setSubject(usersEmail);
        claims.put("roles", roles);

        // Setting the expiration time
        long expirationTime = System.currentTimeMillis() + expirationTimeMs;

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(expirationTime))
                .setHeaderParam("typ", "JWT")
                .setHeaderParam("alg", "HS256")
                .signWith(getSignInKey())
                .compact();
    }

    // 1. JWT Token Generation (Access Token)
    public String generateAccessToken(Authentication authentication) {
        return generateTokenWithExpiration(authentication, jwtExpirationMs);
    }

    // 2. Refresh Token Generation
    public String generateRefreshToken(Authentication authentication) {
        return generateTokenWithExpiration(authentication, refreshTokenExpirationMs);
    }

    // 3. Extracting claims from token
    public Claims extractClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSignInKey())
                    .setAllowedClockSkewSeconds(5)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (JwtException | IllegalArgumentException e) {
            log.error("Failed to parse JWT token: {}", token, e);
            throw new JwtException("Invalid or expired token");
        }
    }

    // 4. Validating the Token
    public boolean validateToken(String tokenValue) {
        if(tokenValue == null)
            return false;
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSignInKey())
                    .build()
                    .parseClaimsJws(tokenValue);
            return true;
        }catch(JwtException e) {
            return false;
        }
    }

    public String getUsernameFromToken(String tokenValue) {
        return extractClaim(tokenValue, Claims::getSubject);
    }




    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // 5. Extract Username (Email) from Token
    public String extractUsernameFromToken(String token) {
        return extractClaims(token).getSubject();
    }

    private LocalDateTime toLocalDateTime(Date date) {
        ZoneOffset zoneOffset = ZoneOffset.UTC;
        return date.toInstant().atOffset(zoneOffset).toLocalDateTime();
    }


    // Unused
    private Date toDate(LocalDateTime localDateTime) {
        ZoneOffset zoneOffset = ZoneOffset.UTC;
        return Date.from(localDateTime.toInstant(zoneOffset));
    }
    public LocalDateTime getExpiryDateFromToken(String tokenValue) {
        return toLocalDateTime(extractClaim(tokenValue, Claims::getExpiration));
    }
}

