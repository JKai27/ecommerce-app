package shopeazy.com.ecommerce_app.order.enums;

/**
 * Represents the current status of an order in the fulfillment lifecycle.
 * Follows a sequential state machine pattern for order processing.
 */
public enum OrderStatus {
    /**
     * Order has been created but payment is pending
     */
    PENDING,
    
    /**
     * Payment confirmed, order is ready for processing
     */
    CONFIRMED,
    
    /**
     * Order is being prepared/packaged by the seller
     */
    PROCESSING,
    
    /**
     * Order has been shipped and is in transit
     */
    SHIPPED,
    
    /**
     * Order has been successfully delivered to customer
     */
    DELIVERED,
    
    /**
     * The Order has been completed (after delivery confirmation period)
     */
    COMPLETED,
    
    /**
     * Order was canceled by customer, seller, or admin
     */
    CANCELLED
}