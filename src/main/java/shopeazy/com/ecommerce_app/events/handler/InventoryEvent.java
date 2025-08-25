package shopeazy.com.ecommerce_app.events.handler;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Map;

/**
 * Event for inventory-related operations.
 * Used to track stock changes, reservations, and releases.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryEvent {
    
    /**
     * Type of inventory event
     */
    private String eventType;
    
    /**
     * Product ID affected
     */
    private String productId;
    
    /**
     * User ID associated with the change (if applicable)
     */
    private String userId;
    
    /**
     * Quantity involved in the operation
     */
    private Integer quantity;
    
    /**
     * Previous stock level
     */
    private Integer previousStock;
    
    /**
     * New stock level
     */
    private Integer newStock;
    
    /**
     * When the event occurred
     */
    private Instant eventTimestamp;
    
    /**
     * Reason for the inventory change
     */
    private String reason;
    
    /**
     * Additional event data
     */
    private Map<String, Object> eventData;
    
    /**
     * Source of the event
     */
    private String source;
    
    /**
     * Correlation ID for event tracing
     */
    private String correlationId;
    
    // Factory methods for common inventory events
    public static InventoryEvent stockReserved(String productId, String userId, Integer quantity, String reason, Map<String, Object> eventData) {
        return new InventoryEvent(
                "INVENTORY_RESERVED",
                productId,
                userId,
                quantity,
                null,
                null,
                Instant.now(),
                reason,
                eventData,
                "INVENTORY_SERVICE",
                generateCorrelationId()
        );
    }
    
    public static InventoryEvent stockReleased(String productId, String userId, Integer quantity, String reason, Map<String, Object> eventData) {
        return new InventoryEvent(
                "INVENTORY_RELEASED",
                productId,
                userId,
                quantity,
                null,
                null,
                Instant.now(),
                reason,
                eventData,
                "INVENTORY_SERVICE",
                generateCorrelationId()
        );
    }
    
    public static InventoryEvent stockUpdated(String productId, Integer previousStock, Integer newStock, String reason, Map<String, Object> eventData) {
        return new InventoryEvent(
                "STOCK_UPDATED",
                productId,
                null,
                newStock - previousStock,
                previousStock,
                newStock,
                Instant.now(),
                reason,
                eventData,
                "INVENTORY_SERVICE",
                generateCorrelationId()
        );
    }
    
    public static InventoryEvent lowStockAlert(String productId, Integer currentStock, Map<String, Object> eventData) {
        return new InventoryEvent(
                "LOW_STOCK_ALERT",
                productId,
                null,
                null,
                null,
                currentStock,
                Instant.now(),
                "Stock level below threshold",
                eventData,
                "INVENTORY_SERVICE",
                generateCorrelationId()
        );
    }
    
    private static String generateCorrelationId() {
        return java.util.UUID.randomUUID().toString();
    }
}