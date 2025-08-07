package shopeazy.com.ecommerce_app.notification.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import shopeazy.com.ecommerce_app.notification.enums.EmailNotificationType;
import shopeazy.com.ecommerce_app.notification.template.EmailTemplateFactory;
import shopeazy.com.ecommerce_app.orders.model.Order;


/**
 * Service for sending email notifications related to orders.
 * Supports both simple text and HTML email templates.
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailNotificationService {
    private final JavaMailSender mailSender;
    @Value("${app.email.from}")
    private String fromEmail;
    private final EmailTemplateFactory emailTemplateFactory;


    public void sendOrderConfirmationEmail(Order order) {
        sendOrderNotificationEmail(order, EmailNotificationType.ORDER_CONFIRMATION);
    }

    public void sendOrderShippedEmail(Order order) {
        sendOrderNotificationEmail(order, EmailNotificationType.ORDER_SHIPPED);
    }

    public void sendOrderCancelledEmail(Order order) {
        sendOrderNotificationEmail(order, EmailNotificationType.ORDER_CANCELLED);
    }

    public void sendOrderDeliveredEmail(Order order) {
        sendOrderNotificationEmail(order, EmailNotificationType.ORDER_DELIVERED);
    }

    /**
     * Send simple text email with boolean validation
     */
    public void sendSimpleEmail(String to, String subject, String text) {
        try {
            if (!isValidEmailInputs(to, subject)) {
                log.error("Cannot send simple email due to validation failures");
                return;
            }

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);

            mailSender.send(message);
            log.info("Simple email sent to {} with subject: {}", to, subject);

        } catch (Exception e) {
            log.error("Failed to send simple email - To: {}, Subject: {}, Error: {}", to, subject, e.getMessage(), e);
        }
    }

    /**
     * Send email with attachment using boolean validation
     */
    public void sendEmailWithAttachment(String to, String subject, String htmlBody,
                                        byte[] attachmentData, String attachmentName) {
        try {
            if (!isValidEmailInputs(to, subject)) {
                log.error("Cannot send email with attachment due to email input validation failures");
                return;
            }

            if (!isValidAttachment(attachmentData, attachmentName)) {
                log.error("Cannot send email with attachment due to attachment validation failures");
                return;
            }

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);

            helper.addAttachment(attachmentName, () -> new java.io.ByteArrayInputStream(attachmentData));

            mailSender.send(message);
            log.info("Email with attachment sent to {} with subject: {}", to, subject);

        } catch (MessagingException e) {
            log.error("Failed to send email with attachment - To: {}, Error: {}", to, e.getMessage(), e);
        } catch (Exception e) {
            log.error("Unexpected error sending email with attachment - To: {}, Error: {}", to, e.getMessage(), e);
        }
    }

    /**
     * Send order notification - TRUE boolean validation approach
     */
    private void sendOrderNotificationEmail(Order order, EmailNotificationType notificationType) {
        try {
            // Boolean validations - no exceptions thrown from validation
            if (!isValidOrder(order)) {
                log.error("Invalid order provided for {}", notificationType.getLogName());
                return; // Exit early, don't send email
            }

            if (!isValidOrderStatus(order, notificationType)) {
                log.error("Order status mismatch for {} - Expected: {}, Actual: {}",
                        notificationType.getLogName(),
                        notificationType.getExpectedStatus(),
                        order.getStatus());
                return; // Exit early
            }

            // Template processing (can still throw - these are system errors, not validation)
            String status = order.getStatus().name();
            String subject = notificationType.formatSubject(order.getOrderNumber());
            String body = emailTemplateFactory.getTemplate(status).buildEmailBody(order);

            // Email sending
            sendHtmlEmail(order.getCustomerEmail(), subject, body);

            log.info("{} email sent successfully - Order: {}, Customer: {}, Status: {}",
                    notificationType.getLogName(), order.getOrderNumber(),
                    order.getCustomerEmail(), order.getStatus());

        } catch (IllegalArgumentException e) {
            // Template not found or factory configuration errors
            log.error("Template/Configuration error for {} (Order: {}): {}",
                    notificationType.getLogName(),
                    order != null ? order.getOrderNumber() : "NULL",
                    e.getMessage());

        } catch (IllegalStateException e) {
            // Multiple templates or other state issues
            log.error("System state error for {} (Order: {}): {}",
                    notificationType.getLogName(),
                    order != null ? order.getOrderNumber() : "NULL",
                    e.getMessage());

        } catch (Exception e) {
            // Email sending errors and other unexpected issues
            log.error("Unexpected error sending {} email (Order: {}): {}",
                    notificationType.getLogName(),
                    order != null ? order.getOrderNumber() : "NULL",
                    e.getMessage(), e);
        }
    }


    public void sendHtmlEmail(String to, String subject, String htmlBody) {
        try {
            if (!isValidEmailInputs(to, subject)) {
                log.error("Cannot send HTML email due to validation failures");
                return;
            }

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);

            mailSender.send(message);
            log.info("HTML email sent to {} with subject: {}", to, subject);

        } catch (MessagingException e) {
            log.error("Failed to send HTML email - To: {}, Subject: {}, Error: {}", to, subject, e.getMessage(), e);
        } catch (Exception e) {
            log.error("Unexpected error sending HTML email - To: {}, Subject: {}, Error: {}", to, subject, e.getMessage(), e);
        }
    }


    private boolean isValidOrder(Order order) {
        if (order == null) {
            log.warn("Order validation failed: Order is null");
            return false;
        }

        if (order.getCustomerEmail() == null || order.getCustomerEmail().trim().isEmpty()) {
            log.warn("Order validation failed: Customer email is null or empty for order {}",
                    order.getOrderNumber());
            return false;
        }

        if (order.getOrderNumber() == null || order.getOrderNumber().trim().isEmpty()) {
            log.warn("Order validation failed: Order number is null or empty");
            return false;
        }

        if (order.getStatus() == null) {
            log.warn("Order validation failed: Order status is null for order {}",
                    order.getOrderNumber());
            return false;
        }

        return true; // All validations passed
    }

    private boolean isValidOrderStatus(Order order, EmailNotificationType notificationType) {
        if (order.getStatus() != notificationType.getExpectedStatus()) {
            log.warn("Order status validation failed: expected {} but got {} for order {}",
                    notificationType.getExpectedStatus(),
                    order.getStatus(),
                    order.getOrderNumber());
            return false;
        }
        return true;
    }

    private boolean isValidEmailInputs(String to, String subject) {
        if (to == null || to.trim().isEmpty()) {
            log.warn("Email validation failed: recipient is null or empty");
            return false;
        }

        if (subject == null || subject.trim().isEmpty()) {
            log.warn("Email validation failed: subject is null or empty");
            return false;
        }

        return true;
    }

    private boolean isValidAttachment(byte[] attachmentData, String attachmentName) {
        if (attachmentData == null || attachmentData.length == 0) {
            log.warn("Attachment validation failed: data is null or empty");
            return false;
        }

        if (attachmentName == null || attachmentName.trim().isEmpty()) {
            log.warn("Attachment validation failed: name is null or empty");
            return false;
        }

        return true;
    }
}
