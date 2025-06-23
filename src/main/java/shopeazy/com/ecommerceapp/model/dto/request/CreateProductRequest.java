package shopeazy.com.ecommerceapp.model.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;
import shopeazy.com.ecommerceapp.enums.ProductStatus;
import shopeazy.com.ecommerceapp.model.document.Product;

import java.util.List;

@Data
public class CreateProductRequest {

    @NotBlank(message = "Product name is required")
    private String name;

    private String description;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be positive")
    private Double price;

    @NotNull(message = "Discount is required")
    @Min(value = 0, message = "Discount cannot be less than 0")
    @Max(value = 100, message = "Discount cannot be greater than 100")
    private Double discount;

    @NotNull(message = "Stock count is required")
    @Min(value = 0, message = "Stock count must be non-negative")
    private Integer stockCount;

    @NotBlank(message = "Category is required")
    private String category;

    @NotEmpty(message = "At least one image is required")
    private List<@NotBlank String> images;

    @NotNull(message = "Status is required")
    private ProductStatus status; // Or ProductStatus if you're validating enums on input

    @NotBlank(message = "Seller ID is required")
    private String sellerId;
}