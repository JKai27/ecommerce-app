package shopeazy.com.ecommerce_app.notification.template;

import org.springframework.stereotype.Component;
import shopeazy.com.ecommerce_app.orders.enums.OrderStatus;
import shopeazy.com.ecommerce_app.orders.model.Order;

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
public class OrderConfirmationTemplateBuilder implements EmailTemplateBuilder {

    @Override
    public String buildEmailBody(Order order) {
        StringBuilder html = new StringBuilder();
        html.append(HTML_START).append("Order Confirmation</title><style>")
                .append(FONT_STYLE)
                .append(STYLE_GREEN_HEADER)
                .append(ORDER_DETAILS_STYLE)
                .append(FOOTER_STYLE)
                .append("</style></head><body>");

        html.append(DIV_HEADER_OPEN)
                .append("<h1>Order Confirmation</h1>")
                .append(DIV_HEADER_CLOSE);

        html.append(DIV_CONTENT_OPEN)
                .append(GREETING).append(order.getCustomerName()).append(",</h2>")
                .append(THANK_YOU_MESSAGE);

        html.append(ORDER_DETAILS_SECTION)
                .append("<h3>Order Details</h3>")
                .append(STRONG_ORDER_NUMBER).append(order.getOrderNumber()).append("</p>")
                .append(STRONG_ORDER_DATE).append(order.getCreatedAt()).append("</p>")
                .append(STRONG_TOTAL_AMOUNT).append(order.getPricing().getTotal()).append("</p>")
                .append(STRONG_STATUS).append(order.getStatus()).append("</p>")
                .append(DIV_CONTENT_CLOSE);

        html.append("<h3>Items Ordered:</h3><ul>");
        order.getOrderItems().forEach(item -> html.append("<li>")
                .append(item.getProductName())
                .append(" (Qty: ").append(item.getQuantity()).append(")")
                .append(" - $").append(item.getTotalPrice())
                .append("</li>")
        );
        html.append("</ul>");

        html.append("<h3>Shipping Address:</h3><p>")
                .append(order.getShippingAddress().getStreet()).append("<br>")
                .append(order.getShippingAddress().getCity()).append(", ")
                .append(order.getShippingAddress().getState()).append(" ")
                .append(order.getShippingAddress().getZip()).append("<br>")
                .append(order.getShippingAddress().getCountry()).append("</p>");

        html.append("<p>We'll send you another email when your order ships!</p>")
                .append(DIV_CONTENT_CLOSE);

        html.append(DIV_FOOTER_OPEN)
                .append(EMAIL_FOOTER)
                .append(CONTACT_INFO)
                .append(DIV_FOOTER_CLOSE);

        html.append(HTML_END);

        return html.toString();

    }

    @Override
    public boolean supports(String status) {
        return OrderStatus.CONFIRMED.name().equalsIgnoreCase(status);
    }

}
