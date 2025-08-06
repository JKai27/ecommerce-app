package shopeazy.com.ecommerce_app.orders.enums;

/**
 * Represents the reason for order cancellation.
 * Used for analytics and customer service.
 */
public enum CancellationReason {
    /**
     * Customer requested cancellation
     */
    CUSTOMER_REQUEST,
    
    /**
     * Item out of stock
     */
    OUT_OF_STOCK,
    
    /**
     * Payment failure
     */
    PAYMENT_FAILED,
    
    /**
     * Seller cannot fulfill order
     */
    SELLER_CANCELLATION,
    
    /**
     * System or administrative cancellation
     */
    SYSTEM_CANCELLATION,
    
    /**
     * Fraud detection triggered
     */
    FRAUD_DETECTION,
    
    /**
     * Delivery address issues
     */
    ADDRESS_ISSUES,
    
    /**
     * Other reasons not listed above
     */
    OTHER
}