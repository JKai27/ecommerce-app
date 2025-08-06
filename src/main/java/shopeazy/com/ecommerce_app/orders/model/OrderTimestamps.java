package shopeazy.com.ecommerce_app.orders.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Tracks all important timestamps throughout the order lifecycle.
 * Embedded document for order timeline tracking.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderTimestamps {
    
    /**
     * When the order was initially created
     */
    private Instant created;
    
    /**
     * When payment was confirmed and order moved to CONFIRMED status
     */
    private Instant confirmed;
    
    /**
     * When order moved to the PROCESSING status (seller started preparing)
     */
    private Instant processed;
    
    /**
     * When the order was shipped
     */
    private Instant shipped;
    
    /**
     * When order was delivered to customer
     */
    private Instant delivered;
    
    /**
     * When the order was canceled (if applicable)
     */
    private Instant cancelled;
    
    /**
     * When the order was completed (final status)
     */
    private Instant completed;
}