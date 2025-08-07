package shopeazy.com.ecommerce_app.notification.template;

import org.springframework.stereotype.Component;
import shopeazy.com.ecommerce_app.orders.enums.OrderStatus;
import shopeazy.com.ecommerce_app.orders.model.Order;

import static shopeazy.com.ecommerce_app.notification.constant.EmailTemplateConstants.*;
import static shopeazy.com.ecommerce_app.notification.constant.EmailTemplateConstants.DELIVERED_MESSAGE;
import static shopeazy.com.ecommerce_app.notification.constant.EmailTemplateConstants.DELIVERY_INFO_SECTION;
import static shopeazy.com.ecommerce_app.notification.constant.EmailTemplateConstants.DELIVERY_INFO_STYLE;
import static shopeazy.com.ecommerce_app.notification.constant.EmailTemplateConstants.DIV_CONTENT_CLOSE;
import static shopeazy.com.ecommerce_app.notification.constant.EmailTemplateConstants.DIV_CONTENT_OPEN;
import static shopeazy.com.ecommerce_app.notification.constant.EmailTemplateConstants.DIV_FOOTER_CLOSE;
import static shopeazy.com.ecommerce_app.notification.constant.EmailTemplateConstants.DIV_FOOTER_OPEN;
import static shopeazy.com.ecommerce_app.notification.constant.EmailTemplateConstants.DIV_HEADER_CLOSE;
import static shopeazy.com.ecommerce_app.notification.constant.EmailTemplateConstants.DIV_HEADER_OPEN;
import static shopeazy.com.ecommerce_app.notification.constant.EmailTemplateConstants.FEEDBACK_INVITE;
import static shopeazy.com.ecommerce_app.notification.constant.EmailTemplateConstants.FOOTER_STYLE;
import static shopeazy.com.ecommerce_app.notification.constant.EmailTemplateConstants.GREETING;
import static shopeazy.com.ecommerce_app.notification.constant.EmailTemplateConstants.HTML_END;
import static shopeazy.com.ecommerce_app.notification.constant.EmailTemplateConstants.STRONG_ORDER_NUMBER;

@Component
public class OrderDeliveredTemplateBuilder implements EmailTemplateBuilder {
    @Override
    public String buildEmailBody(Order order) {
        StringBuilder html = new StringBuilder();
        html.append(HTML_START).append("Order Delivered</title><style>")
                .append(FONT_STYLE)
                .append(STYLE_GREEN_HEADER)
                .append(DELIVERY_INFO_STYLE)
                .append(FOOTER_STYLE)
                .append("</style></head><body>");

        html.append(DIV_HEADER_OPEN)
                .append("<h1>Order Delivered Successfully!</h1>")
                .append(DIV_HEADER_CLOSE);

        html.append(DIV_CONTENT_OPEN)
                .append(GREETING).append(order.getCustomerName()).append(",</h2>")
                .append(DELIVERED_MESSAGE)
                .append(STRONG_ORDER_NUMBER).append(order.getOrderNumber()).append("</p>");

        if (order.getTrackingInfo() != null && order.getTrackingInfo().getActualDelivery() != null) {
            html.append(DELIVERY_INFO_SECTION)
                    .append("<h3>Delivery Information</h3>")
                    .append("<p><strong>Delivered On:</strong> ").append(order.getTrackingInfo().getActualDelivery()).append("</p>");
            if (order.getTrackingInfo().getReceivedBy() != null) {
                html.append("<p><strong>Received By:</strong> ").append(order.getTrackingInfo().getReceivedBy()).append("</p>");
            }
            html.append(DIV_CONTENT_CLOSE);
        }

        html.append("<p>We hope you're satisfied with your purchase! If you have any issues, please don't hesitate to contact us.</p>")
                .append("<p>Thank you for choosing ShopEazy!</p>")
                .append(DIV_CONTENT_CLOSE);

        html.append(DIV_FOOTER_OPEN)
                .append(FEEDBACK_INVITE)
                .append(DIV_FOOTER_CLOSE);

        html.append(HTML_END);

        return html.toString();

    }

    @Override
    public boolean supports(String status) {
        return OrderStatus.DELIVERED.name().equalsIgnoreCase(status);
    }
}
