package shopeazy.com.ecommerce_app.order.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for cancelling an order.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CancelOrderRequest {
    
    /**
     * Reason for cancelling the order
     */
    @NotBlank(message = "Cancellation reason is required")
    private String reason;
}