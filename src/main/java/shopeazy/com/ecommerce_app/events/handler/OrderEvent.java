package shopeazy.com.ecommerce_app.events.handler;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import shopeazy.com.ecommerce_app.order.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;

/**
 * Base class for all order-related events in the system.
 * Used for event-driven architecture with Kafka.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderEvent {
    
    /**
     * Type of event (ORDER_CREATED, ORDER_CONFIRMED, etc.)
     */
    private String eventType;
    
    /**
     * Order ID that triggered the event
     */
    private String orderId;
    
    /**
     * Order number (human-readable)
     */
    private String orderNumber;
    
    /**
     * User ID who owns the order
     */
    private String userId;
    
    /**
     * Customer email for notifications
     */
    private String customerEmail;
    
    /**
     * Current order status
     */
    private OrderStatus orderStatus;
    
    /**
     * Total order amount
     */
    private BigDecimal totalAmount;
    
    /**
     * Currency code
     */
    private String currency;
    
    /**
     * When the event occurred
     */
    private Instant eventTimestamp;
    
    /**
     * Additional event-specific data
     */
    private Map<String, Object> eventData;
    
    /**
     * Source system/service that generated the event
     */
    private String source;
    
    /**
     * Event correlation ID for tracing
     */
    private String correlationId;
    
    // Static factory methods for common events
    public static OrderEvent orderCreated(String orderId, String orderNumber, String userId, String customerEmail, 
                                         BigDecimal totalAmount, String currency, Map<String, Object> eventData) {
        return new OrderEvent(
                "ORDER_CREATED",
                orderId,
                orderNumber,
                userId,
                customerEmail,
                OrderStatus.PENDING,
                totalAmount,
                currency,
                Instant.now(),
                eventData,
                "ORDER_SERVICE",
                generateCorrelationId()
        );
    }
    
    public static OrderEvent orderConfirmed(String orderId, String orderNumber, String userId, String customerEmail, 
                                          BigDecimal totalAmount, String currency, Map<String, Object> eventData) {
        return new OrderEvent(
                "ORDER_CONFIRMED",
                orderId,
                orderNumber,
                userId,
                customerEmail,
                OrderStatus.CONFIRMED,
                totalAmount,
                currency,
                Instant.now(),
                eventData,
                "ORDER_SERVICE",
                generateCorrelationId()
        );
    }
    
    public static OrderEvent orderProcessing(String orderId, String orderNumber, String userId, String customerEmail, 
                                           BigDecimal totalAmount, String currency, Map<String, Object> eventData) {
        return new OrderEvent(
                "ORDER_PROCESSING",
                orderId,
                orderNumber,
                userId,
                customerEmail,
                OrderStatus.PROCESSING,
                totalAmount,
                currency,
                Instant.now(),
                eventData,
                "ORDER_SERVICE",
                generateCorrelationId()
        );
    }
    
    public static OrderEvent orderCancelled(String orderId, String orderNumber, String userId, String customerEmail,
                                          BigDecimal totalAmount, String currency, Map<String, Object> eventData) {
        return new OrderEvent(
                "ORDER_CANCELLED",
                orderId,
                orderNumber,
                userId,
                customerEmail,
                OrderStatus.CANCELLED,
                totalAmount,
                currency,
                Instant.now(),
                eventData,
                "ORDER_SERVICE",
                generateCorrelationId()
        );
    }
    
    public static OrderEvent orderShipped(String orderId, String orderNumber, String userId, String customerEmail,
                                        BigDecimal totalAmount, String currency, Map<String, Object> eventData) {
        return new OrderEvent(
                "ORDER_SHIPPED",
                orderId,
                orderNumber,
                userId,
                customerEmail,
                OrderStatus.SHIPPED,
                totalAmount,
                currency,
                Instant.now(),
                eventData,
                "ORDER_SERVICE",
                generateCorrelationId()
        );
    }
    
    public static OrderEvent orderDelivered(String orderId, String orderNumber, String userId, String customerEmail,
                                          BigDecimal totalAmount, String currency, Map<String, Object> eventData) {
        return new OrderEvent(
                "ORDER_DELIVERED",
                orderId,
                orderNumber,
                userId,
                customerEmail,
                OrderStatus.DELIVERED,
                totalAmount,
                currency,
                Instant.now(),
                eventData,
                "ORDER_SERVICE",
                generateCorrelationId()
        );
    }
    
    private static String generateCorrelationId() {
        return java.util.UUID.randomUUID().toString();
    }
}