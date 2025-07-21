package shopeazy.com.ecommerce_app.product.dto;

import lombok.Data;

@Data
public class ProductAvailabilityResponse {
    private boolean isAvailable;
    private Integer productStockCount;
    private String message;
}
