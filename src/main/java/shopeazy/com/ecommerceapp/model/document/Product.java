package shopeazy.com.ecommerceapp.model.document;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import shopeazy.com.ecommerceapp.enums.ProductStatus;

import java.time.Instant;
import java.util.List;

@Document
@Data
public class Product {
    private String id;
    private String name;
    private String description;
    private Double price;
    private String stock;
    private Double discount;
    private List<String> images;
    private String sellerId;
    private ProductStatus status;
    private Instant createdAt;
    private Instant updatedAt;
}
