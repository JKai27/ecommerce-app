package shopeazy.com.ecommerce_app.notification.template;

import org.springframework.stereotype.Component;
import shopeazy.com.ecommerce_app.order.enums.OrderStatus;
import shopeazy.com.ecommerce_app.order.model.Order;

import static shopeazy.com.ecommerce_app.notification.constant.EmailTemplateConstants.*;
import static shopeazy.com.ecommerce_app.notification.constant.EmailTemplateConstants.DIV_CONTENT_CLOSE;
import static shopeazy.com.ecommerce_app.notification.constant.EmailTemplateConstants.DIV_CONTENT_OPEN;
import static shopeazy.com.ecommerce_app.notification.constant.EmailTemplateConstants.DIV_FOOTER_CLOSE;
import static shopeazy.com.ecommerce_app.notification.constant.EmailTemplateConstants.DIV_FOOTER_OPEN;
import static shopeazy.com.ecommerce_app.notification.constant.EmailTemplateConstants.DIV_HEADER_CLOSE;
import static shopeazy.com.ecommerce_app.notification.constant.EmailTemplateConstants.DIV_HEADER_OPEN;
import static shopeazy.com.ecommerce_app.notification.constant.EmailTemplateConstants.EMAIL_FOOTER;
import static shopeazy.com.ecommerce_app.notification.constant.EmailTemplateConstants.FOOTER_STYLE;
import static shopeazy.com.ecommerce_app.notification.constant.EmailTemplateConstants.GREETING;
import static shopeazy.com.ecommerce_app.notification.constant.EmailTemplateConstants.HTML_END;
import static shopeazy.com.ecommerce_app.notification.constant.EmailTemplateConstants.SHIPPED_MESSAGE;
import static shopeazy.com.ecommerce_app.notification.constant.EmailTemplateConstants.STRONG_ORDER_NUMBER;
import static shopeazy.com.ecommerce_app.notification.constant.EmailTemplateConstants.TRACKING_INFO_SECTION;
import static shopeazy.com.ecommerce_app.notification.constant.EmailTemplateConstants.TRACKING_INFO_STYLE;

@Component
public class OrderShippedTemplateBuilder implements EmailTemplateBuilder {
    @Override
    public String buildEmailBody(Order order) {
        StringBuilder html = new StringBuilder();

        html.append(HTML_START).append("Order Shipped</title><style>")
                .append(FONT_STYLE)
                .append(STYLE_BLUE_HEADER)
                .append(TRACKING_INFO_STYLE)
                .append(FOOTER_STYLE)
                .append("</style></head><body>");

        html.append(DIV_HEADER_OPEN)
                .append("<h1>Your Order Has Been Shipped!</h1>")
                .append(DIV_HEADER_CLOSE);

        html.append(DIV_CONTENT_OPEN)
                .append(GREETING).append(order.getCustomerName()).append(",</h2>")
                .append(SHIPPED_MESSAGE)
                .append(STRONG_ORDER_NUMBER).append(order.getOrderNumber()).append("</p>");

        if (order.getTrackingInfo() != null) {
            html.append(TRACKING_INFO_SECTION)
                    .append("<h3>Tracking Information</h3>")
                    .append("<p><strong>Tracking Number:</strong> ").append(order.getTrackingInfo().getTrackingNumber()).append("</p>")
                    .append("<p><strong>Carrier:</strong> ").append(order.getTrackingInfo().getCarrier()).append("</p>");
            if (order.getTrackingInfo().getEstimatedDelivery() != null) {
                html.append("<p><strong>Estimated Delivery:</strong> ").append(order.getTrackingInfo().getEstimatedDelivery()).append("</p>");
            }
            html.append(DIV_CONTENT_CLOSE);
        }

        html.append("<p>You can track your package using the tracking number provided above.</p>")
                .append(DIV_CONTENT_CLOSE);

        html.append(DIV_FOOTER_OPEN)
                .append(EMAIL_FOOTER)
                .append(DIV_FOOTER_CLOSE);

        html.append(HTML_END);

        return html.toString();
    }

    @Override
    public boolean supports(String status) {
        return OrderStatus.SHIPPED.name().equalsIgnoreCase(status);
    }
}
