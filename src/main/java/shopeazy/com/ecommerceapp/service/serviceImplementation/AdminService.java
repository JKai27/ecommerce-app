package shopeazy.com.ecommerceapp.service.serviceImplementation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import shopeazy.com.ecommerceapp.repository.UserRepository;
import shopeazy.com.ecommerceapp.jwt.JwtService;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminService {
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final UserService userService;



}
