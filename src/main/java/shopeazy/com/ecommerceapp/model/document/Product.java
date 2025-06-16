package shopeazy.com.ecommerceapp.model.document;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document
@Data
public class Product {
    private String id;
    private String title;
    private String description;
    private Double price;
    private String stock;
    private String sellerId;

    private Instant createdAt;
}
