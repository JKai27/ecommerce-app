package shopeazy.com.ecommerce_app.order.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import shopeazy.com.ecommerce_app.common.model.Address;
import shopeazy.com.ecommerce_app.order.enums.OrderStatus;
import shopeazy.com.ecommerce_app.order.enums.PaymentStatus;

import java.time.Instant;
import java.util.List;

/**
 * Represents a customer order in the e-commerce system.
 * This entity contains all information about an order including items,
 * pricing, addresses, and status tracking.
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "order")
public class Order {
    @Id
    private String id;

    /**
     * Human-readable order number
     */
    @Indexed(unique = true)
    private String orderNumber;

    /**
     * Customer who places the order
     */
    private String userId;

    private String customerEmail;
    private String customerName;

    /**
     * List of items in the order
     */
    private List<OrderItem> orderItems;

    /**
     * Pricing breakdown for the order
     */
    private OrderPricing pricing;


    private Address shippingAddress;

    /**
     * Billing address (can be the same as shipping)
     */
    private Address billingAddress;

    @Indexed
    private OrderStatus status;

    @Indexed
    private PaymentStatus paymentStatus;

    /**
     * Order lifecycle timestamps
     */
    private OrderTimestamps timestamps;

    /**
     * Shipping and tracking information
     */
    private TrackingInfo trackingInfo;

    /**
     * Cancellation details (if order was canceled)
     */
    private CancellationInfo cancellationInfo;

    /**
     * Payment transaction ID from payment gateway
     */
    private String paymentTransactionId;

    /**
     * Additional notes or special instructions
     */
    private String notes;

    /**
     * Order creation timestamp (managed by MongoDB)
     */
    @CreatedDate
    private Instant createdAt;

    /**
     * Last modification timestamp (managed by MongoDB)
     */
    @LastModifiedDate
    private Instant updatedAt;

    /**
     * Calculate the total number of items in the order
     */
    public int getTotalItemCount() {
        return orderItems != null ? orderItems.stream()
                .mapToInt(OrderItem::getQuantity)
                .sum() : 0;
    }

    /**
     * Checks if order can be canceled based on current status
     */
    public boolean canBeCancelled() {
        return status == OrderStatus.PENDING ||
                status == OrderStatus.CONFIRMED ||
                status == OrderStatus.PROCESSING;
    }

    /**
     * Checks if order has been shipped
     */

    public boolean isShipped() {
        return status == OrderStatus.SHIPPED ||
                status == OrderStatus.DELIVERED ||
                status == OrderStatus.COMPLETED;
    }

    /**
     * Checks if order is in a final state
     */
    public boolean isFinalState() {
        return status == OrderStatus.DELIVERED ||
                status == OrderStatus.COMPLETED ||
                status == OrderStatus.CANCELLED;
    }

}
