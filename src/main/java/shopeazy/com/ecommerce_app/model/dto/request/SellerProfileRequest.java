package shopeazy.com.ecommerce_app.model.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SellerProfileRequest {
    private String companyName;
    @NotBlank
    @Email
    private String contactEmail;
}
