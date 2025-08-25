package shopeazy.com.ecommerce_app.order.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Contains shipping and tracking information for an order.
 * Embedded document for order tracking details.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrackingInfo {
    
    /**
     * Tracking number provided by shipping carrier
     */
    private String trackingNumber;
    
    /**
     * Shipping carrier name (e.g., "FedEx", "UPS", "DHL")
     */
    private String carrier;
    
    /**
     * Estimated delivery date
     */
    private Instant estimatedDelivery;
    
    /**
     * Actual delivery date (when available)
     */
    private Instant actualDelivery;
    
    /**
     * Name of the person who received the delivery
     */
    private String receivedBy;
    
    /**
     * Additional tracking notes or updates
     */
    private String notes;
}