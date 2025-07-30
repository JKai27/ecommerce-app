package shopeazy.com.ecommerce_app.product.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import shopeazy.com.ecommerce_app.product.enums.ProductStatus;

import java.util.List;
@Data
public class BulkUpdateProductStatusRequest {

    @NotEmpty(message = "Product ID list must not be empty")
    private List<String> productIds;

    @NotNull(message = "Status must not be null")
    private ProductStatus status;
}
