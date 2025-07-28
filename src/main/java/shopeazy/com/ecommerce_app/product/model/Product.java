package shopeazy.com.ecommerce_app.product.model;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import jakarta.validation.constraints.*;
import shopeazy.com.ecommerce_app.product.enums.ProductStatus;

import java.time.Instant;
import java.util.List;

@Data
@Document
public class Product {

    @Id
    private String id;

    private String productNumber;

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
    @CreatedDate
    private Instant createdAt;
    @LastModifiedDate
    private Instant updatedAt;
}