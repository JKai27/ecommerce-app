package shopeazy.com.ecommerce_app.seller.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SellerApprovalRequest {
    @NotNull
    private String userEmail;
    @NotNull
    private String contactEmail;
}
