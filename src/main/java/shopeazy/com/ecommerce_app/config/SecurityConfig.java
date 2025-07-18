package shopeazy.com.ecommerce_app.config;


import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import shopeazy.com.ecommerce_app.security.config.CustomAccessDeniedHandler;
import shopeazy.com.ecommerce_app.security.config.CustomAuthenticationEntryPoint;
import shopeazy.com.ecommerce_app.security.jwt.JwtAuthFilter;
import shopeazy.com.ecommerce_app.security.jwt.JwtService;

import static shopeazy.com.ecommerce_app.common.util.EndpointPaths.*;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private static final String ROLE_ADMIN = "ADMIN";
    private static final String ROLE_SELLER = "SELLER";


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PUBLIC_ENDPOINTS).permitAll()
                        .requestMatchers(HttpMethod.GET, ADMIN_GET_ENDPOINTS).hasRole(ROLE_ADMIN)
                        .requestMatchers(HttpMethod.POST, SELLER_ENDPOINTS).hasRole(ROLE_SELLER)
                        .requestMatchers(HttpMethod.DELETE, ADMIN_DELETE_ENDPOINTS).hasRole(ROLE_ADMIN)
                        .requestMatchers(HttpMethod.DELETE, PRODUCT_IMAGES_WILDCARD).hasAnyRole(ROLE_SELLER, ROLE_ADMIN)
                        .requestMatchers(HttpMethod.PATCH, ADMIN_PATCH_ENDPOINTS).hasRole(ROLE_ADMIN)
                        .requestMatchers(HttpMethod.PUT, ADMIN_PUT_ENDPOINTS).hasRole(ROLE_ADMIN)
                        .requestMatchers("/api/auth/user/**").hasRole(ROLE_ADMIN)

                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exception -> exception
                        .accessDeniedHandler(new CustomAccessDeniedHandler())
                        .authenticationEntryPoint(new CustomAuthenticationEntryPoint())
                )
                .addFilterBefore(new JwtAuthFilter(jwtService, userDetailsService), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:3000"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        config.setAllowCredentials(true);
        config.setAllowedHeaders(List.of("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
