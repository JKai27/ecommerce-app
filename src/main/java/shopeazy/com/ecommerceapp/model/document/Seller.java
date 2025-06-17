package shopeazy.com.ecommerceapp.model.document;

import org.springframework.data.mongodb.core.mapping.Document;


import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Field;
import shopeazy.com.ecommerceapp.enums.SellerStatus;

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

    private SellerStatus status = SellerStatus.PENDING;

    private String userId;
}
