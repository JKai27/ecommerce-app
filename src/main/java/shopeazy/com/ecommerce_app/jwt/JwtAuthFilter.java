package shopeazy.com.ecommerce_app.jwt;

import com.mongodb.lang.NonNull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/*

GOOD TO KNOW
In Spring Boot filters
registered as @Component or via FilterRegistrationBean are part of the Servlet container’s filter chain
and execute before Spring Security.
These filters are not aware of Spring Security’s SecurityContext,
unless you manually add them to the Spring Security filter chain using HttpSecurity.addFilterBefore(...).
For anything involving authentication or authorization,
it’s essential to register filters explicitly in the security filter chain to ensure correct behavior and ordering.

*/

@Slf4j
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;



    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        // Step 1: Extract JWT from Cookies
        String token = extractJwtFromCookies(request);

        // Validate and clear cookie
        if (token != null && !jwtService.validateToken(token)) {
            log.warn("Expired or invalid token received: clearing cookie.");
            Cookie expiredCookie = new Cookie("access_token", null);
            expiredCookie.setPath("/");
            expiredCookie.setHttpOnly(true);
            expiredCookie.setSecure(true);
            expiredCookie.setMaxAge(0);
            response.addCookie(expiredCookie);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        // Step 2: Validate the Token
        if (token != null && jwtService.validateToken(token)) {
            // Step 3: Extract the Username (Email) from the token
            String username = jwtService.extractUsernameFromToken(token);

            // get User roles and permissions from a database and populate security context
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // Step 4: Set Authentication in SecurityContext if valid
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities()
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);  // Store authentication in context

            log.info("Authenticated user: {}", username);
        }

        // Step 5: Continue the filter chain
        filterChain.doFilter(request, response);
    }

    // Helper method to extract JWT from cookies
    private String extractJwtFromCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("access_token".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

}