package shopeazy.com.ecommerce_app.seller.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SellerRejectionRequest {
    @NotBlank(message = "Rejection reason must not be blank") // confirm with Kevin: should reason be mandatory
    private String reason;
}