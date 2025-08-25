package shopeazy.com.ecommerce_app.order.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import shopeazy.com.ecommerce_app.order.enums.OrderStatus;

/**
 * Request DTO for updating order status (admin operation).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateOrderStatusRequest {
    
    /**
     * New status for the order
     */
    @NotNull(message = "New status is required")
    private OrderStatus newStatus;
}