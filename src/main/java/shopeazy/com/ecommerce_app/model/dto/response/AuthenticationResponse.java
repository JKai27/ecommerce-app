package shopeazy.com.ecommerce_app.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthenticationResponse {
    private String jwtToken;
    private String userId;
}
