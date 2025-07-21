package shopeazy.com.ecommerce_app.product.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import shopeazy.com.ecommerce_app.product.enums.ProductStatus;

@Data
public class UpdateProductRequestDto {
    @NotNull(message = "Product ID is required")
    private String productId;

    private String name;
    private String description;

    @Positive(message = "Price must be positive")
    private Double price;

    @Min(value = 0, message = "Discount cannot be less than 0")
    @Max(value = 100, message = "Discount cannot be greater than 100")
    private Double discount;

    @Min(value = 0, message = "Stock count must be positive or 0")
    private int stockCount;

    private String category;
    private ProductStatus status;
}