package shopeazy.com.ecommerce_app.notification.template;

import shopeazy.com.ecommerce_app.order.model.Order;

public interface EmailTemplateBuilder {
    String buildEmailBody(Order order);
    boolean supports(String status);
}
