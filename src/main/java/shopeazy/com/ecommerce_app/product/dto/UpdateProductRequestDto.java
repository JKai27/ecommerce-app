package shopeazy.com.ecommerce_app.product.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import shopeazy.com.ecommerce_app.product.enums.ProductStatus;

@Data
public class UpdateProductRequestDto {
    @NotNull(message = "Product ID is required")
    private String productId;

    private String name;
    private String description;
    private Double price;
    private Double discount;
    private Integer stockCount;
    private String category;
    private ProductStatus status;
}