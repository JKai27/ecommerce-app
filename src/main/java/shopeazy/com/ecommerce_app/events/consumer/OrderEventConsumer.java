package shopeazy.com.ecommerce_app.events.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import shopeazy.com.ecommerce_app.events.handler.OrderEvent;
import shopeazy.com.ecommerce_app.notification.service.EmailNotificationService;
import shopeazy.com.ecommerce_app.order.model.Order;
import shopeazy.com.ecommerce_app.order.repository.OrderRepository;
import shopeazy.com.ecommerce_app.pdf.service.PdfGenerationService;

/**
 * Kafka consumer for order-related events.
 * Handles order lifecycle events and triggers appropriate actions.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventConsumer {

    private final EmailNotificationService emailService;
    private final PdfGenerationService pdfService;
    private final OrderRepository orderRepository;

    @KafkaListener(topics = "order-events", groupId = "order-notification-group")
    public void handleOrderEvent(@Payload OrderEvent event,
                                 @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {

        try {
            log.info("Received order event: {} for order {}", event.getEventType(), event.getOrderNumber());

            switch (event.getEventType()) {
                case "ORDER_CREATED" -> handleOrderCreated(event);
                case "ORDER_CONFIRMED" -> handleOrderConfirmed(event);
                case "ORDER_CANCELLED" -> handleOrderCancelled(event);
                case "ORDER_SHIPPED" -> handleOrderShipped(event);
                case "ORDER_DELIVERED" -> handleOrderDelivered(event);
                default -> log.warn("Unknown order event type: {}", event.getEventType());
            }

            // Message automatically acknowledged on successful completion

        } catch (Exception e) {
            log.error("Error processing order event {}: {}", event.getEventType(), e.getMessage(), e);
            // Re-throw to trigger retry mechanism
        }
    }

    private void handleOrderCreated(OrderEvent event) {
        try {
            // Find the order
            Order order = orderRepository.findById(event.getOrderId()).orElse(null);
            if (order == null) {
                log.warn("Order not found for ORDER_CREATED event: {}", event.getOrderId());
                return;
            }

            log.info("Processing ORDER_CREATED for order {}", event.getOrderNumber());

            // Send order confirmation email (without PDF for pending order)
            emailService.sendOrderConfirmationEmail(order);

            log.info("Successfully processed ORDER_CREATED for order {}", event.getOrderNumber());

        } catch (Exception e) {
            log.error("Error handling ORDER_CREATED event for order {}: {}", event.getOrderNumber(), e.getMessage(), e);
            throw e; // Re-throw to prevent acknowledgment
        }
    }

    private void handleOrderConfirmed(OrderEvent event) {
        try {
            // Find the order
            Order order = orderRepository.findById(event.getOrderId()).orElse(null);
            if (order == null) {
                log.warn("Order not found for ORDER_CONFIRMED event: {}", event.getOrderId());
                return;
            }

            log.info("Processing ORDER_CONFIRMED for order {}", event.getOrderNumber());

            // Generate invoice PDF
            byte[] invoicePdf = pdfService.generateInvoicePdf(order);

            // Send confirmation email with PDF attachment
            String subject = "Order Confirmed - " + order.getOrderNumber();
            String htmlBody = buildOrderConfirmedEmailBody(order);

            emailService.sendEmailWithAttachment(
                    order.getCustomerEmail(),
                    subject,
                    htmlBody,
                    invoicePdf,
                    "invoice-" + order.getOrderNumber() + ".pdf"
            );

            log.info("Successfully processed ORDER_CONFIRMED for order {}", event.getOrderNumber());

        } catch (Exception e) {
            log.error("Error handling ORDER_CONFIRMED event for order {}: {}", event.getOrderNumber(), e.getMessage(), e);
            throw e; // Re-throw to prevent acknowledgment
        }
    }

    private void handleOrderCancelled(OrderEvent event) {
        try {
            // Find the order
            Order order = orderRepository.findById(event.getOrderId()).orElse(null);
            if (order == null) {
                log.warn("Order not found for ORDER_CANCELLED event: {}", event.getOrderId());
                return;
            }

            log.info("Processing ORDER_CANCELLED for order {}", event.getOrderNumber());

            // Send cancellation email
            emailService.sendOrderCancelledEmail(order);

            // Generate cancellation receipt PDF if needed
            if (order.getCancellationInfo() != null && order.getCancellationInfo().getRefundAmount() != null) {
                byte[] receiptPdf = pdfService.generateCancellationReceiptPdf(order);

                String subject = "Cancellation Receipt - " + order.getOrderNumber();
                String htmlBody = buildCancellationReceiptEmailBody(order);

                emailService.sendEmailWithAttachment(
                        order.getCustomerEmail(),
                        subject,
                        htmlBody,
                        receiptPdf,
                        "cancellation-receipt-" + order.getOrderNumber() + ".pdf"
                );
            }

            log.info("Successfully processed ORDER_CANCELLED for order {}", event.getOrderNumber());

        } catch (Exception e) {
            log.error("Error handling ORDER_CANCELLED event for order {}: {}", event.getOrderNumber(), e.getMessage(), e);
            throw e; // Re-throw to prevent acknowledgment
        }
    }

    private void handleOrderShipped(OrderEvent event) {
        try {
            // Find the order
            Order order = orderRepository.findById(event.getOrderId()).orElse(null);
            if (order == null) {
                log.warn("Order not found for ORDER_SHIPPED event: {}", event.getOrderId());
                return;
            }

            log.info("Processing ORDER_SHIPPED for order {}", event.getOrderNumber());

            // Send shipping notification email
            emailService.sendOrderShippedEmail(order);

            log.info("Successfully processed ORDER_SHIPPED for order {}", event.getOrderNumber());

        } catch (Exception e) {
            log.error("Error handling ORDER_SHIPPED event for order {}: {}", event.getOrderNumber(), e.getMessage(), e);
            throw e; // Re-throw to prevent acknowledgment
        }
    }

    private void handleOrderDelivered(OrderEvent event) {
        try {
            // Find the order
            Order order = orderRepository.findById(event.getOrderId()).orElse(null);
            if (order == null) {
                log.warn("Order not found for ORDER_DELIVERED event: {}", event.getOrderId());
                return;
            }

            log.info("Processing ORDER_DELIVERED for order {}", event.getOrderNumber());

            // Send delivery confirmation email
            emailService.sendOrderDeliveredEmail(order);

            log.info("Successfully processed ORDER_DELIVERED for order {}", event.getOrderNumber());

        } catch (Exception e) {
            log.error("Error handling ORDER_DELIVERED event for order {}: {}", event.getOrderNumber(), e.getMessage(), e);
            throw e; // Re-throw to prevent acknowledgment
        }
    }

    // Email template builders for specific events

    private String buildOrderConfirmedEmailBody(Order order) {
        return """
                <h2>Order Confirmed!</h2>
                <p>Dear %s,</p>
                <p>Your payment has been processed and your order is confirmed!</p>
                <p><strong>Order Number:</strong> %s</p>
                <p><strong>Total Amount:</strong> $%s</p>
                <p>Please find your invoice attached to this email.</p>
                <p>We'll notify you when your order ships.</p>
                <p>Thank you for shopping with ShopEazy!</p>
                """.formatted(
                order.getCustomerName(),
                order.getOrderNumber(),
                order.getPricing().getTotal()
        );
    }

    private String buildCancellationReceiptEmailBody(Order order) {
        String refundInfo = "";
        if (order.getCancellationInfo() != null && order.getCancellationInfo().getRefundAmount() != null) {
            refundInfo = "<p><strong>Refund Amount:</strong> $" + order.getCancellationInfo().getRefundAmount() + "</p>";
        }

        return """
                <h2>Order Cancellation Receipt</h2>
                <p>Dear %s,</p>
                <p>Your order has been cancelled as requested.</p>
                <p><strong>Order Number:</strong> %s</p>
                %s
                <p>Please find your cancellation receipt attached to this email.</p>
                <p>If you have any questions, please contact our customer service.</p>
                """.formatted(
                order.getCustomerName(),
                order.getOrderNumber(),
                refundInfo
        );
    }
}