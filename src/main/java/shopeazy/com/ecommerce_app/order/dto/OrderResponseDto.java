package shopeazy.com.ecommerce_app.order.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import shopeazy.com.ecommerce_app.common.model.Address;
import shopeazy.com.ecommerce_app.order.enums.OrderStatus;
import shopeazy.com.ecommerce_app.order.enums.PaymentStatus;
import shopeazy.com.ecommerce_app.order.model.*;

import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponseDto {

    private String id;
    private String orderNumber;
    private String userId;
    private String customerEmail;
    private String customerName;
    private List<OrderItem> items;
    private OrderPricing pricing;
    private Address shippingAddress;
    private Address billingAddress;
    private OrderStatus status;
    private PaymentStatus paymentStatus;
    private OrderTimestamps timestamps;
    private TrackingInfo trackingInfo;
    private CancellationInfo cancellationInfo;
    private String paymentTransactionId;
    private String notes;
    private Instant createdAt;
    private Instant updatedAt;
    private int totalItemCount;
    private boolean canBeCancelled;
    private boolean isShipped;
    private boolean isFinalState;
}
