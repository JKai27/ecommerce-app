package shopeazy.com.ecommerce_app.product.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import jakarta.validation.constraints.*;
import shopeazy.com.ecommerce_app.product.enums.ProductStatus;

import java.time.Instant;
import java.util.List;

@Document
@Data
public class Product {

    @Id
    private String id;

    @NotBlank
    private String name;

    private String description;

    @NotNull
    @Min(0)
    private Double price;

    @NotNull
    @Min(0)
    @Max(99)
    private Double discount;

    @NotNull
    @Min(0)
    private Integer stockCount;

    @NotBlank
    private String category;

    @NotEmpty
    private List<String> images;

    @NotBlank
    private String sellerId;

    @NotNull
    private ProductStatus status;

    private Instant createdAt;
    private Instant updatedAt;
}