package shopeazy.com.ecommerce_app.events.handler;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Map;

/**
 * Event for triggering notifications (email, SMS, push, etc.).
 * Used to decouple notification sending from business logic.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationEvent {
    
    /**
     * Type of notification event
     */
    private String eventType;
    
    /**
     * Recipient email address
     */
    private String recipientEmail;
    
    /**
     * Recipient user ID
     */
    private String recipientUserId;
    
    /**
     * Subject line for email notifications
     */
    private String subject;
    
    /**
     * Template name to use for the notification
     */
    private String templateName;
    
    /**
     * Template data/variables for personalization
     */
    private Map<String, Object> templateData;
    
    /**
     * When the notification event was created
     */
    private Instant eventTimestamp;
    
    /**
     * Priority level (HIGH, MEDIUM, LOW)
     */
    private String priority;
    
    /**
     * Source system that generated the notification
     */
    private String source;
    
    /**
     * Correlation ID for tracking
     */
    private String correlationId;
    
    /**
     * Whether to attach files (PDF invoices, etc.)
     */
    private boolean hasAttachments;
    
    /**
     * Attachment information
     */
    private Map<String, Object> attachmentData;
    
    // Factory methods for common notification events
    public static NotificationEvent orderConfirmationEmail(String recipientEmail, String recipientUserId, 
                                                          Map<String, Object> orderData, boolean attachInvoice) {
        return new NotificationEvent(
                "ORDER_CONFIRMATION_EMAIL",
                recipientEmail,
                recipientUserId,
                "Order Confirmation - " + orderData.get("orderNumber"),
                "order-confirmation",
                orderData,
                Instant.now(),
                "HIGH",
                "ORDER_SERVICE",
                generateCorrelationId(),
                attachInvoice,
                attachInvoice ? Map.of("invoicePdf", true, "orderId", orderData.get("orderId")) : null
        );
    }
    
    public static NotificationEvent orderShippedEmail(String recipientEmail, String recipientUserId, 
                                                     Map<String, Object> shippingData) {
        return new NotificationEvent(
                "ORDER_SHIPPED_EMAIL",
                recipientEmail,
                recipientUserId,
                "Your Order Has Been Shipped - " + shippingData.get("orderNumber"),
                "order-shipped",
                shippingData,
                Instant.now(),
                "MEDIUM",
                "ORDER_SERVICE",
                generateCorrelationId(),
                false,
                null
        );
    }
    
    public static NotificationEvent orderCancelledEmail(String recipientEmail, String recipientUserId, 
                                                       Map<String, Object> cancellationData) {
        return new NotificationEvent(
                "ORDER_CANCELLED_EMAIL",
                recipientEmail,
                recipientUserId,
                "Order Cancellation Notice - " + cancellationData.get("orderNumber"),
                "order-cancelled",
                cancellationData,
                Instant.now(),
                "HIGH",
                "ORDER_SERVICE",
                generateCorrelationId(),
                false,
                null
        );
    }
    
    public static NotificationEvent orderDeliveredEmail(String recipientEmail, String recipientUserId, 
                                                       Map<String, Object> deliveryData) {
        return new NotificationEvent(
                "ORDER_DELIVERED_EMAIL",
                recipientEmail,
                recipientUserId,
                "Order Delivered - " + deliveryData.get("orderNumber"),
                "order-delivered",
                deliveryData,
                Instant.now(),
                "MEDIUM",
                "ORDER_SERVICE",
                generateCorrelationId(),
                false,
                null
        );
    }
    
    public static NotificationEvent lowStockAlert(String recipientEmail, String recipientUserId, 
                                                 Map<String, Object> stockData) {
        return new NotificationEvent(
                "LOW_STOCK_ALERT",
                recipientEmail,
                recipientUserId,
                "Low Stock Alert - " + stockData.get("productName"),
                "low-stock-alert",
                stockData,
                Instant.now(),
                "MEDIUM",
                "INVENTORY_SERVICE",
                generateCorrelationId(),
                false,
                null
        );
    }
    
    private static String generateCorrelationId() {
        return java.util.UUID.randomUUID().toString();
    }
}