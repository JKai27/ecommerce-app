package shopeazy.com.ecommerce_app.events.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import shopeazy.com.ecommerce_app.common.exception.ResourceNotFoundException;
import shopeazy.com.ecommerce_app.events.handler.InventoryEvent;
import shopeazy.com.ecommerce_app.notification.service.EmailNotificationService;
import shopeazy.com.ecommerce_app.product.model.Product;
import shopeazy.com.ecommerce_app.product.repository.ProductRepository;
import shopeazy.com.ecommerce_app.seller.model.Seller;
import shopeazy.com.ecommerce_app.seller.repository.SellerProfileRepository;
import shopeazy.com.ecommerce_app.user.model.User;
import shopeazy.com.ecommerce_app.user.repository.UserRepository;


@Slf4j
@Component
@RequiredArgsConstructor
public class InventoryEventConsumer {

    private final ProductRepository productRepository;
    private final SellerProfileRepository sellerProfileRepository;
    private final UserRepository userRepository;
    private final EmailNotificationService emailService;

    @KafkaListener(topics = "inventory-events", groupId = "inventory-management-group")
    public void handleInventoryEvent(@Payload InventoryEvent event,
                                     @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        try {
            log.info("Received Inventory Event: {} for product {} ", event, event.getProductId());

            switch (event.getEventType()) {
                case "INVENTORY_RESERVED" -> handleInventoryReserved(event);
                case "INVENTORY_RELEASED" -> handleInventoryReleased(event);
                case "STOCK_UPDATED" -> handleStockUpdated(event);
                case "LOW_STOCK_ALERT" -> handleLowStockAlert(event);
                default -> log.warn("Unknown inventory event type: {}", event.getEventType());
            }
        } catch (Exception exception) {
            log.error("Error while processing Inventory Event {} : {}", event.getEventType(), exception.getMessage(), exception);
        }
    }


    private void handleInventoryReserved(InventoryEvent event) {
        try {
            log.info("Processing INVENTORY_RESERVED for product {} by user {}",
                    event.getProductId(), event.getUserId());

            log.debug("Inventory reserved: {} units of product {} for user {}",
                    event.getQuantity(), event.getProductId(), event.getUserId());

        } catch (Exception exception) {
            log.error("Error handling INVENTORY_RESERVED event: {}", exception.getMessage(), exception);
            throw exception;
        }
    }

    private void handleInventoryReleased(InventoryEvent event) {
        try {
            log.info("Processing INVENTORY_RELEASED event for product {} by user {}", event.getProductId(), event.getUserId());

            // Check if this was due to cart timeout and potentially notify user
            if ("CART_TIMEOUT".equals(event.getReason())) {
                handleCartTimeout(event);
            }

            log.debug("Inventory released: {} units of product {} for user {}, reason: {}",
                    event.getQuantity(), event.getProductId(), event.getUserId(), event.getReason());

        } catch (Exception exception) {
            log.error("Error handling INVENTORY_RELEASED event: {}", exception.getMessage());
            throw exception;
        }
    }


    private void handleCartTimeout(InventoryEvent event) {
        try {
            // Find user and send notification about cart timeout
            if (event.getUserId() != null) {
                User user = userRepository.findById(event.getUserId()).orElseThrow(ResourceNotFoundException::new);
                Product product = productRepository.findById(event.getProductId()).orElseThrow(ResourceNotFoundException::new);

                if (user != null && product != null) {
                    String subject = "Items Removed from Cart - Reservation Expired";
                    String message = String.format(
                            """
                                    Dear %s,
                                    
                                    The following item was removed from your cart due to reservation timeout:
                                    - %s (Quantity: %d)
                                    
                                    The items are now available for other customers. \
                                    If you still want to purchase them, please add them back to your cart.
                                    
                                    Thank you for shopping with ShopEazy!""",
                            user.getFirstName() + " " + user.getLastName(),
                            product.getName(),
                            event.getQuantity()
                    );

                    emailService.sendSimpleEmail(user.getEmail(), subject, message);

                    log.info("Sent cart timeout notification to user {}", user.getEmail());
                }
            }

        } catch (Exception e) {
            log.error("Error handling cart timeout notification: {}", e.getMessage(), e);
        }
    }

    private void handleStockUpdated(InventoryEvent event) {
        try {
            log.info("Processing STOCK_UPDATED for product {}: {} → {}",
                    event.getProductId(), event.getPreviousStock(), event.getNewStock());

            Product product = productRepository.findById(event.getProductId()).orElse(null);
            if (product == null) {
                log.warn("Product not found for STOCK_UPDATED event: {}", event.getProductId());
                return;
            }

            // Check for low stock condition
            int lowStockThreshold = 5; // Could be configurable
            if (event.getNewStock() != null && event.getNewStock() <= lowStockThreshold &&
                    (event.getPreviousStock() == null || event.getPreviousStock() > lowStockThreshold)) {

                // Trigger low stock alert
                sendLowStockAlert(product, event.getNewStock());
            }

            // Check if item is back in stock (for wishlist notifications)
            if (event.getPreviousStock() != null && event.getPreviousStock() == 0 &&
                    event.getNewStock() != null && event.getNewStock() > 0) {

                handleBackInStock(product);
            }

        } catch (Exception e) {
            log.error("Error handling STOCK_UPDATED event: {}", e.getMessage(), e);
            throw e;
        }
    }


    private void handleLowStockAlert(InventoryEvent event) {
        try {
            log.info("Processing LOW_STOCK_ALERT for product {}", event.getProductId());

            Product product = productRepository.findById(event.getProductId()).orElseThrow(ResourceNotFoundException::new);
            if (product == null) {
                log.warn("Product not found for LOW_STOCK_ALERT event: {}", event.getProductId());
                return;
            }

            sendLowStockAlert(product, event.getNewStock());

        } catch (Exception e) {
            log.error("Error handling LOW_STOCK_ALERT event: {}", e.getMessage(), e);
            throw e;
        }
    }


    private void sendLowStockAlert(Product product, Integer currentStock) {
        try {
            // Find the seller and send low stock alert
            Seller seller = sellerProfileRepository.findById(product.getSellerId()).orElseThrow(ResourceNotFoundException::new);
            if (seller != null) {
                String subject = "Low Stock Alert - " + product.getName();
                String message = String.format(
                        """
                                Dear Seller,
                                
                                Your product is running low on stock:
                                
                                Product: %s
                                Current Stock: %d units
                                Product ID: %s
                                
                                Please consider restocking this item to avoid lost sales.
                                
                                Best regards,
                                ShopEazy Team""",
                        product.getName(),
                        currentStock != null ? currentStock : 0,
                        product.getId()
                );

                emailService.sendSimpleEmail(seller.getContactEmail(), subject, message);

                log.info("Sent low stock alert to seller {} for product {}",
                        seller.getContactEmail(), product.getName());
            }

        } catch (Exception e) {
            log.error("Error sending low stock alert: {}", e.getMessage(), e);
            // Don't re-throw here as this is a notification, not critical
        }
    }

    private void handleBackInStock(Product product) {
        try {
            log.info("Product {} is back in stock with {} units", product.getName(), product.getStockCount());

            // This would typically integrate with a wishlist system
            // to notify users who have this product in their wishlist

            // For now, just log the event
            // In a complete implementation, you would:
            // 1. Find users with this product in their wishlist
            // 2. Send "back in stock" notification emails
            // 3. Optionally create a queue for first-come-first-served purchasing

            log.debug("Back in stock notification would be sent for product {}", product.getId());

        } catch (Exception e) {
            log.error("Error handling back in stock event: {}", e.getMessage(), e);
        }
    }

}
