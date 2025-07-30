package shopeazy.com.ecommerce_app.product.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import shopeazy.com.ecommerce_app.product.enums.ProductStatus;

@Data
public class UpdateProductStatusRequest {
    @NotBlank(message = "Product ID must not be blank")
    private String productId;

    @NotNull(message = "Status must not be null")
    private ProductStatus status;
}
