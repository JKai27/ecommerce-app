package shopeazy.com.ecommerce_app.model.dto.request;

import lombok.Data;

@Data
public class SellerApprovalRequest {
    private String userEmail;
    private String contactEmail;
}
