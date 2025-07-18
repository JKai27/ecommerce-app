package shopeazy.com.ecommerce_app.admin.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import shopeazy.com.ecommerce_app.user.repository.UserRepository;
import shopeazy.com.ecommerce_app.security.jwt.JwtService;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminService {
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final shopeazy.com.ecommerce_app.user.service.UserServiceImpl userServiceImpl;



}
