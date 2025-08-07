package shopeazy.com.ecommerce_app.notification.template;

import org.springframework.stereotype.Component;
import shopeazy.com.ecommerce_app.orders.enums.OrderStatus;
import shopeazy.com.ecommerce_app.orders.model.Order;

import static shopeazy.com.ecommerce_app.notification.constant.EmailTemplateConstants.*;
import static shopeazy.com.ecommerce_app.notification.constant.EmailTemplateConstants.CANCELLATION_MESSAGE;
import static shopeazy.com.ecommerce_app.notification.constant.EmailTemplateConstants.CONTACT_INFO;
import static shopeazy.com.ecommerce_app.notification.constant.EmailTemplateConstants.DIV_CONTENT_CLOSE;
import static shopeazy.com.ecommerce_app.notification.constant.EmailTemplateConstants.DIV_CONTENT_OPEN;
import static shopeazy.com.ecommerce_app.notification.constant.EmailTemplateConstants.DIV_FOOTER_CLOSE;
import static shopeazy.com.ecommerce_app.notification.constant.EmailTemplateConstants.DIV_FOOTER_OPEN;
import static shopeazy.com.ecommerce_app.notification.constant.EmailTemplateConstants.DIV_HEADER_CLOSE;
import static shopeazy.com.ecommerce_app.notification.constant.EmailTemplateConstants.DIV_HEADER_OPEN;
import static shopeazy.com.ecommerce_app.notification.constant.EmailTemplateConstants.FOOTER_STYLE;
import static shopeazy.com.ecommerce_app.notification.constant.EmailTemplateConstants.GREETING;
import static shopeazy.com.ecommerce_app.notification.constant.EmailTemplateConstants.HTML_END;
import static shopeazy.com.ecommerce_app.notification.constant.EmailTemplateConstants.REFUND_INFO_SECTION;
import static shopeazy.com.ecommerce_app.notification.constant.EmailTemplateConstants.REFUND_INFO_STYLE;
import static shopeazy.com.ecommerce_app.notification.constant.EmailTemplateConstants.STRONG_ORDER_NUMBER;
import static shopeazy.com.ecommerce_app.notification.constant.EmailTemplateConstants.STRONG_TOTAL_AMOUNT;

@Component
public class OrderCancelledTemplateBuilder implements EmailTemplateBuilder {
    @Override
    public String buildEmailBody(Order order) {
        StringBuilder html = new StringBuilder();
        html.append(HTML_START).append("Order Cancelled</title><style>")
                .append(FONT_STYLE)
                .append(STYLE_RED_HEADER)
                .append(REFUND_INFO_STYLE)
                .append(FOOTER_STYLE)
                .append("</style></head><body>");

        html.append(DIV_HEADER_OPEN)
                .append("<h1>Order Cancellation Notice</h1>")
                .append(DIV_HEADER_CLOSE);

        html.append(DIV_CONTENT_OPEN)
                .append(GREETING).append(order.getCustomerName()).append(",</h2>")
                .append(CANCELLATION_MESSAGE)
                .append(STRONG_ORDER_NUMBER).append(order.getOrderNumber()).append("</p>")
                .append(STRONG_TOTAL_AMOUNT).append(order.getPricing().getTotal()).append("</p>");

        if (order.getCancellationInfo() != null) {
            html.append(REFUND_INFO_SECTION)
                    .append("<h3>Cancellation Details</h3>")
                    .append("<p><strong>Reason:</strong> ").append(order.getCancellationInfo().getReason()).append("</p>");
            if (order.getCancellationInfo().getDetails() != null) {
                html.append("<p><strong>Details:</strong> ").append(order.getCancellationInfo().getDetails()).append("</p>");
            }
            if (order.getCancellationInfo().getRefundAmount() != null) {
                html.append("<p><strong>Refund Amount:</strong> $").append(order.getCancellationInfo().getRefundAmount()).append("</p>")
                        .append("<p>Your refund will be processed within 3-5 business days.</p>");
            }
            html.append(DIV_CONTENT_CLOSE);
        }

        html.append("<p>We apologize for any inconvenience this may have caused.</p>")
                .append(DIV_CONTENT_CLOSE);

        html.append(DIV_FOOTER_OPEN)
                .append(CONTACT_INFO)
                .append(DIV_FOOTER_CLOSE);

        html.append(HTML_END);

        return html.toString();
    }

    @Override
    public boolean supports(String status) {
        return OrderStatus.CANCELLED.name().equalsIgnoreCase(status);
    }
}
