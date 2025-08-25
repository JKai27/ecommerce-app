package shopeazy.com.ecommerce_app.notification.constant;

public class EmailTemplateConstants {

    public static final String UTF_8 = "UTF-8";

    public static final String HTML_START = "<!DOCTYPE html><html><head><title>";
    public static final String HTML_END = "</body></html>";
    public static final String FONT_STYLE = "body { font-family: Arial, sans-serif; margin: 20px; color: #333; }";
    public static final String FOOTER_STYLE = ".footer { text-align: center; padding: 20px; color: #666; font-size: 12px; }";

    public static final String STYLE_GREEN_HEADER = ".header { background-color: #4CAF50; color: white; padding: 20px; text-align: center; }";
    public static final String STYLE_BLUE_HEADER = ".header { background-color: #2196F3; color: white; padding: 20px; text-align: center; }";
    public static final String STYLE_RED_HEADER = ".header { background-color: #f44336; color: white; padding: 20px; text-align: center; }";

    public static final String DIV_HEADER_OPEN = "<div class='header'>";
    public static final String DIV_HEADER_CLOSE = "</div>";
    public static final String DIV_CONTENT_OPEN = "<div class='content'>";
    public static final String DIV_CONTENT_CLOSE = "</div>";
    public static final String DIV_FOOTER_OPEN = "<div class='footer'>";
    public static final String DIV_FOOTER_CLOSE = "</div>";

    public static final String GREETING = "<h2>Dear ";
    public static final String THANK_YOU_MESSAGE = "<p>Thank you for your order! We've received your order and it's being processed.</p>";
    public static final String EMAIL_FOOTER = "<p>Thank you for shopping with ShopEazy!</p>";
    public static final String CONTACT_INFO = "<p>If you have any questions, please contact our customer service.</p>";

    public static final String STRONG_ORDER_NUMBER = "<p><strong>Order Number:</strong> ";
    public static final String STRONG_ORDER_DATE = "<p><strong>Order Date:</strong> ";
    public static final String STRONG_TOTAL_AMOUNT = "<p><strong>Total Amount:</strong> $";
    public static final String STRONG_STATUS = "<p><strong>Status:</strong> ";
    public static final String ORDER_DETAILS_SECTION = "<div class='order-details'>";
    public static final String ORDER_DETAILS_STYLE = ".order-details { background-color: #f9f9f9; padding: 15px; margin: 10px 0; }";

    public static final String REFUND_INFO_SECTION = "<div class='refund-info'>";
    public static final String REFUND_INFO_STYLE = ".refund-info { background-color: #ffebee; padding: 15px; margin: 10px 0; border-left: 4px solid #f44336; }";
    public static final String CANCELLATION_MESSAGE = "<p>We're writing to inform you that your order has been cancelled.</p>";

    public static final String DELIVERY_INFO_SECTION = "<div class='delivery-info'>";
    public static final String DELIVERY_INFO_STYLE = ".delivery-info { background-color: #f1f8e9; padding: 15px; margin: 10px 0; border-left: 4px solid #4CAF50; }";
    public static final String DELIVERED_MESSAGE = "<p>Your order has been successfully delivered!</p>";

    public static final String FEEDBACK_INVITE = "<p>We'd love to hear about your experience. Consider leaving a review!</p>";
    public static final String TRACKING_INFO_SECTION = "<div class='tracking-info'>";
    public static final String TRACKING_INFO_STYLE = ".tracking-info { background-color: #e3f2fd; padding: 15px; margin: 10px 0; border-left: 4px solid #2196F3; }";
    public static final String SHIPPED_MESSAGE = "<p>Great news! Your order has been shipped and is on its way to you.</p>";



    private EmailTemplateConstants() {
    }
}
