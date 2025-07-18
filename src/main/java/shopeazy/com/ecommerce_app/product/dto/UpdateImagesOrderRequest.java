package shopeazy.com.ecommerce_app.product.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class UpdateImagesOrderRequest {
    @NotEmpty(message = "Image URL list must not be empty")
    private List<String> orderedImageUrls;
}
