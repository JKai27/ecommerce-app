package shopeazy.com.ecommerce_app.order.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import shopeazy.com.ecommerce_app.order.enums.OrderStatus;
import shopeazy.com.ecommerce_app.order.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Lightweight order summary DTO for list views.
 * Contains essential order information without full details.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderSummaryDto {
    
    private String id;
    private String orderNumber;
    private OrderStatus status;
    private PaymentStatus paymentStatus;
    private BigDecimal totalAmount;
    private String currency;
    private int totalItemCount;
    private Instant createdAt;
    private Instant estimatedDelivery;
    private boolean canBeCancelled;
    private String trackingNumber;
}