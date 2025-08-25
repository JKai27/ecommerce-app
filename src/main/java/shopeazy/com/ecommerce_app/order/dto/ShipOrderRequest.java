package shopeazy.com.ecommerce_app.order.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for shipping an order.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShipOrderRequest {
    
    /**
     * Tracking number from the shipping carrier
     */
    @NotBlank(message = "Tracking number is required")
    private String trackingNumber;
    
    /**
     * Shipping carrier name (e.g., "FedEx", "UPS", "DHL")
     */
    @NotBlank(message = "Carrier name is required")
    private String carrier;
}