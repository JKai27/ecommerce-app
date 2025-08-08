package shopeazy.com.ecommerce_app.seller.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SellerProfileRequest {
    @NotNull
    private String companyName;
    @NotNull
    @Email
    private String contactEmail;
}
