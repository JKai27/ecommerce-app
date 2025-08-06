package shopeazy.com.ecommerce_app.orders.enums;

/**
 * Represents the payment status of an order.
 * Used to track payment processing lifecycle.
 */
public enum PaymentStatus {
    /**
     * Payment is awaiting processing
     */
    PENDING,
    
    /**
     * Payment has been successfully processed
     */
    PAID,
    
    /**
     * Payment processing failed
     */
    FAILED,
    
    /**
     * Payment was refunded to customer
     */
    REFUNDED,
    
    /**
     * Partial refund was processed
     */
    PARTIALLY_REFUNDED
}