package shopeazy.com.ecommerce_app.service.implementation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import shopeazy.com.ecommerce_app.repository.UserRepository;
import shopeazy.com.ecommerce_app.jwt.JwtService;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminService {
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final UserServiceImpl userServiceImpl;



}
