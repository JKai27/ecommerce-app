package shopeazy.com.ecommerce_app.seller.dto;

import lombok.Data;

@Data
public class SellerApprovalRequest {
    private String userEmail;
    private String contactEmail;
}
