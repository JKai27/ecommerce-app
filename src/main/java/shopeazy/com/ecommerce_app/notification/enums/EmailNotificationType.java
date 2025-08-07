package shopeazy.com.ecommerce_app.notification.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shopeazy.com.ecommerce_app.orders.enums.OrderStatus;

@Getter
@AllArgsConstructor
public enum EmailNotificationType {
    ORDER_CONFIRMATION("Order confirmation", "Order Confirmation - %s", OrderStatus.CONFIRMED),
    ORDER_SHIPPED("Order shipped", "Your order has been shipped - %s", OrderStatus.SHIPPED),
    ORDER_CANCELLED("Order cancelled", "Order Cancellation Notice - %s", OrderStatus.CANCELLED),
    ORDER_DELIVERED("Order delivered", "Order Delivered - %s", OrderStatus.DELIVERED);

    private final String logName;
    private final String subjectTemplate;
    private final OrderStatus expectedStatus;


    public String formatSubject(String orderNumber) {
        return String.format(subjectTemplate, orderNumber);
    }

}
