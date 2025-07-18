package shopeazy.com.ecommerce_app.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TokenPair {
    private String jwtToken;
    private String refreshToken;
}

