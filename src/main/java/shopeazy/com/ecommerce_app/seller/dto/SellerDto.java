package shopeazy.com.ecommerce_app.seller.dto;

import lombok.Data;
import shopeazy.com.ecommerce_app.seller.enums.SellerStatus;

import java.time.Instant;

@Data
public class SellerDto {
    private String sellerId;
    private String sellerNumber;
    private String companyName;
    private String contactEmail;
    private int productCount;
    private Instant registeredAt;
    private SellerStatus sellerStatus;
}