package shopeazy.com.ecommerce_app.model.document;

import org.springframework.data.mongodb.core.mapping.Document;


import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import shopeazy.com.ecommerce_app.enums.SellerStatus;

import java.time.Instant;

@Data
@Document(collection = "seller-profiles")
public class Seller {
    @Id
    private String sellerId;

    @Indexed(unique = true)
    private String sellerNumber;

    @Indexed(unique = true)
    private String companyName;

    @Indexed(unique = true)
    private String contactEmail;

    private int productCount;

    private Instant registeredAt;

    private SellerStatus sellerStatus = SellerStatus.PENDING;

    private String userId;
}
