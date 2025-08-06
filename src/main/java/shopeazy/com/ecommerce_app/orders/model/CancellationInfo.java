package shopeazy.com.ecommerce_app.orders.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import shopeazy.com.ecommerce_app.orders.enums.CancellationReason;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Contains information about order cancellation.
 * Embedded document that stores cancellation details and the refund information.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CancellationInfo {
    
    /**
     * Reason for cancellation
     */
    private CancellationReason reason;
    
    /**
     * Additional details or notes about cancellation
     */
    private String details;
    
    /**
     * Who initiated the cancellation (USER, SELLER, ADMIN, SYSTEM)
     */
    private String cancelledBy;
    
    /**
     * ID of the user/entity who canceled the order
     */
    private String cancelledById;
    
    /**
     * When the cancellation was processed
     */
    private Instant cancelledAt;
    
    /**
     * Status of refund processing
     */
    private String refundStatus;
    
    /**
     * Amount refunded to customer
     */
    private BigDecimal refundAmount;
    
    /**
     * When the refund was processed
     */
    private Instant refundProcessedAt;
    
    /**
     * Reference ID for the refund transaction
     */
    private String refundTransactionId;
}