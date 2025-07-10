package shopeazy.com.ecommerce_app.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RefreshTokenRequest {
    private String refreshToken;
}
