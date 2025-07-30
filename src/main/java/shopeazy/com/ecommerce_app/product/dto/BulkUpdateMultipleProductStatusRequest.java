package shopeazy.com.ecommerce_app.product.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class BulkUpdateMultipleProductStatusRequest {
    @NotEmpty
    private List<UpdateProductStatusRequest> updates;
}