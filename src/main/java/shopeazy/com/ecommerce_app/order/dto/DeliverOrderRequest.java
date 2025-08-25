package shopeazy.com.ecommerce_app.order.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for marking an order as delivered.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeliverOrderRequest {
    
    /**
     * Name of the person who received the delivery
     */
    @NotBlank(message = "Received by field is required")
    private String receivedBy;
}