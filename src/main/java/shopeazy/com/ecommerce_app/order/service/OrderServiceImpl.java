package shopeazy.com.ecommerce_app.order.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shopeazy.com.ecommerce_app.common.UniqueReadableNumberService;
import shopeazy.com.ecommerce_app.common.exception.ResourceNotFoundException;
import shopeazy.com.ecommerce_app.events.handler.OrderEvent;
import shopeazy.com.ecommerce_app.inventory.service.InventoryReservationService;
import shopeazy.com.ecommerce_app.order.dto.CreateOrderRequest;
import shopeazy.com.ecommerce_app.order.dto.OrderResponseDto;
import shopeazy.com.ecommerce_app.order.dto.OrderStatisticsDto;
import shopeazy.com.ecommerce_app.order.dto.OrderSummaryDto;
import shopeazy.com.ecommerce_app.order.dto.ProcessOrderRequest;
import shopeazy.com.ecommerce_app.order.enums.CancellationReason;
import shopeazy.com.ecommerce_app.order.enums.OrderStatus;
import shopeazy.com.ecommerce_app.order.enums.PaymentStatus;
import shopeazy.com.ecommerce_app.order.exception.InvalidOrderStatusException;
import shopeazy.com.ecommerce_app.order.exception.OrderNotFoundException;
import shopeazy.com.ecommerce_app.order.model.*;
import shopeazy.com.ecommerce_app.order.repository.OrderRepository;
import shopeazy.com.ecommerce_app.product.model.Product;
import shopeazy.com.ecommerce_app.product.repository.ProductRepository;
import shopeazy.com.ecommerce_app.seller.model.Seller;
import shopeazy.com.ecommerce_app.seller.repository.SellerProfileRepository;
import shopeazy.com.ecommerce_app.shopping_cart.model.Cart;
import shopeazy.com.ecommerce_app.shopping_cart.model.pojo.CartItem;
import shopeazy.com.ecommerce_app.shopping_cart.repository.CartRepository;
import shopeazy.com.ecommerce_app.user.model.User;
import shopeazy.com.ecommerce_app.user.repository.UserRepository;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final UniqueReadableNumberService numberService;
    private final SellerProfileRepository sellerRepository;
    private final ModelMapper modelMapper;
    private final InventoryReservationService inventoryReservationService;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public OrderResponseDto createOrderFromCart(CreateOrderRequest request, String userEmail) {
        try {
            User user = userRepository.findByEmail(userEmail).orElseThrow(ResourceNotFoundException::new);

            Cart cart = cartRepository.findByUserId(user.getId()).orElseThrow(ResourceNotFoundException::new);

            if (cart.getItems() == null || cart.getItems().isEmpty()) {
                throw new IllegalStateException("Cannot create order from empty cart");
            }

            // validate cart items and inventory
            if (!validateCartForOrder(user.getId())) {
                throw new IllegalStateException("Cart validation failed - insufficient inventory");
            }

            // Validate payment transaction ID format
            validatePaymentTransactionId(request.getPaymentTransactionId());
            // Create order
            Order order = new Order();
            int orderSequence = numberService.getNextSequence("ORDER");
            order.setOrderNumber(String.format("ORD-%06d", orderSequence));
            order.setUserId(user.getId());
            order.setCustomerEmail(user.getEmail());
            order.setCustomerName(user.getFirstName() + " " + user.getLastName());
            order.setStatus(OrderStatus.PENDING);
            order.setPaymentStatus(PaymentStatus.PENDING);
            order.setPaymentTransactionId(request.getPaymentTransactionId());
            order.setNotes(request.getNotes());

            // Set addresses
            order.setShippingAddress(request.getShippingAddress());
            order.setBillingAddress(request.isUseSameAddressForBilling() ?
                    request.getShippingAddress() : request.getBillingAddress());

            // Convert cart items to order items
            List<OrderItem> orderItems = convertCartItemsToOrderItems(cart.getItems());
            
            // Validate inventory availability for order items
            validateInventoryForOrder(orderItems);
            
            order.setOrderItems(orderItems);

            // Calculate pricing
            OrderPricing pricing = calculateOrderPricing(orderItems);
            order.setPricing(pricing);

            // Set timestamps
            OrderTimestamps timestamps = new OrderTimestamps();
            timestamps.setCreated(Instant.now());
            order.setTimestamps(timestamps);

            // Save order
            order = orderRepository.save(order);

            // Convert cart reservations to order reservations
            inventoryReservationService.convertToOrderReservations(user.getId(), order.getId());

            // Clear the cart
            cart.getItems().clear();
            cartRepository.save(cart);

            // Publish order created event
            publishOrderCreatedEvent(order);

            log.info("Created order {} for user {}", order.getOrderNumber(), userEmail);

            return mapToOrderResponseDto(order);

        } catch (Exception e) {
            log.error("Error creating order for user {}: {}", userEmail, e.getMessage(), e);
            throw new RuntimeException("Failed to create order: " + e.getMessage());
        }

    }

    @Override
    public OrderResponseDto getOrderById(String orderId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found"));

        // Check if user owns this order or is a seller for any items
        if (!canUserAccessOrder(order, user)) {
            throw new RuntimeException("Access denied to this order");
        }

        return mapToOrderResponseDto(order);
    }

    @Override
    public OrderResponseDto getOrderByOrderNumber(String orderNumber, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new OrderNotFoundException("Order not found"));

        if (!canUserAccessOrder(order, user)) {
            throw new RuntimeException("Access denied to this order");
        }

        return mapToOrderResponseDto(order);
    }

    @Override
    public Page<OrderSummaryDto> getUserOrders(String userEmail, Pageable pageable) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Page<Order> orders = orderRepository.findByUserIdOrderByCreatedAtDesc(user.getId(), pageable);

        return orders.map(this::mapToOrderSummaryDto);
    }

    @Override
    public Page<OrderSummaryDto> getUserOrdersByStatus(String userEmail, OrderStatus status, Pageable pageable) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Page<Order> orders = orderRepository.findByUserIdAndStatusOrderByCreatedAtDesc(
                user.getId(), status, pageable);

        return orders.map(this::mapToOrderSummaryDto);
    }

    @Override
    public Page<OrderSummaryDto> getSellerOrders(String sellerEmail, Pageable pageable) {
        userRepository.findByEmail(sellerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Seller seller = sellerRepository.findByContactEmail(sellerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Seller profile not found"));

        Page<Order> orders = orderRepository.findOrdersContainingSellerProducts(seller.getSellerId(), pageable);

        return orders.map(this::mapToOrderSummaryDto);
    }

    @Override
    public Page<OrderSummaryDto> getSellerOrdersByStatus(String sellerEmail, OrderStatus status, Pageable pageable) {
        userRepository.findByEmail(sellerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Seller seller = sellerRepository.findByContactEmail(sellerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Seller profile not found"));

        Page<Order> orders = orderRepository.findOrdersContainingSellerProductsWithStatus(
                seller.getSellerId(), status, pageable);

        return orders.map(this::mapToOrderSummaryDto);
    }


    @Override
    public OrderResponseDto confirmOrder(String orderId, String userEmail) {
        Order order = getOrderForUser(orderId, userEmail);

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new InvalidOrderStatusException("Order cannot be confirmed in current status: " + order.getStatus());
        }

        // Update order status and timestamps
        order.setStatus(OrderStatus.CONFIRMED);
        order.setPaymentStatus(PaymentStatus.PAID);
        order.getTimestamps().setConfirmed(Instant.now());

        // Re-validate inventory before confirming (prevent race conditions)
        validateInventoryForOrder(order.getOrderItems());

        // Update product stock counts
        updateProductStockAfterOrder(order);

        order = orderRepository.save(order);

        // Publish order confirmed event
        publishOrderConfirmedEvent(order);

        log.info("Confirmed order {} for user {}", order.getOrderNumber(), userEmail);

        return mapToOrderResponseDto(order);
    }

    @Override
    public OrderResponseDto processOrder(String orderId, ProcessOrderRequest request, String sellerEmail) {
        // Get the seller
        User seller = userRepository.findByEmail(sellerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Seller not found"));

        Seller sellerProfile = sellerRepository.findByUserId(seller.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Seller profile not found"));

        // Get the order
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found"));

        // Validate order can be processed
        if (order.getStatus() != OrderStatus.CONFIRMED) {
            throw new InvalidOrderStatusException("Order must be in CONFIRMED status to process");
        }

        // Verify seller has products in this order
        boolean hasSellerProducts = order.getOrderItems().stream()
                .anyMatch(item -> {
                    Product product = productRepository.findById(item.getProductId()).orElse(null);
                    return product != null && product.getSellerId().equals(sellerProfile.getSellerId());
                });

        if (!hasSellerProducts) {
            throw new InvalidOrderStatusException("Seller does not have any products in this order");
        }

        // Update order status
        order.setStatus(OrderStatus.PROCESSING);
        order.getTimestamps().setProcessed(Instant.now());

        // Add processing notes if provided
        if (request.getProcessingNotes() != null) {
            order.setNotes((order.getNotes() != null ? order.getNotes() + "\n" : "") + 
                    "Processing Notes: " + request.getProcessingNotes());
        }

        order = orderRepository.save(order);

        // Publish order processing event
        publishOrderProcessingEvent(order, sellerEmail);

        log.info("Order {} marked as processing by seller {}", order.getOrderNumber(), sellerEmail);

        return mapToOrderResponseDto(order);
    }

    @Override
    public OrderResponseDto cancelOrder(String orderId, String userEmail, String reason) {
        Order order = getOrderForUser(orderId, userEmail);

        if (!order.canBeCancelled()) {
            throw new InvalidOrderStatusException("Order cannot be cancelled in current status: " + order.getStatus());
        }

        // Set cancellation info
        CancellationInfo cancellationInfo = new CancellationInfo();
        cancellationInfo.setReason(CancellationReason.CUSTOMER_REQUEST);
        cancellationInfo.setDetails(reason);
        cancellationInfo.setCancelledBy("USER");
        cancellationInfo.setCancelledById(order.getUserId());
        cancellationInfo.setCancelledAt(Instant.now());
        cancellationInfo.setRefundStatus("PENDING");
        cancellationInfo.setRefundAmount(order.getPricing().getTotal());

        order.setCancellationInfo(cancellationInfo);
        order.setStatus(OrderStatus.CANCELLED);
        order.getTimestamps().setCancelled(Instant.now());

        // Release inventory reservations
        inventoryReservationService.releaseAllUserReservations(order.getUserId());

        // Restore product stock if order was confirmed
        if (order.getPaymentStatus() == PaymentStatus.PAID) {
            restoreProductStockAfterCancellation(order);
        }

        order = orderRepository.save(order);

        // Publish order canceled event
        publishOrderCancelledEvent(order);

        log.info("Cancelled order {} for user {}", order.getOrderNumber(), userEmail);

        return mapToOrderResponseDto(order);
    }

    @Override
    public OrderResponseDto updateOrderStatus(String orderId, OrderStatus newStatus, String updatedBy) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found"));

        validateStatusTransition(order.getStatus(), newStatus);

        OrderStatus previousStatus = order.getStatus();
        order.setStatus(newStatus);

        // Update timestamps based on new status
        updateTimestampsForStatus(order, newStatus);

        order = orderRepository.save(order);

        // Publish appropriate event based on status change
        publishStatusChangeEvent(order, previousStatus, updatedBy);

        log.info("Updated order {} status from {} to {} by {}",
                order.getOrderNumber(), previousStatus, newStatus, updatedBy);

        return mapToOrderResponseDto(order);
    }

    @Override
    public OrderResponseDto shipOrder(String orderId, String trackingNumber, String carrier, String sellerEmail) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found"));

        if (order.getStatus() != OrderStatus.PROCESSING) {
            throw new InvalidOrderStatusException("Order must be in PROCESSING status to ship");
        }

        // Create tracking info
        TrackingInfo trackingInfo = new TrackingInfo();
        trackingInfo.setTrackingNumber(trackingNumber);
        trackingInfo.setCarrier(carrier);
        trackingInfo.setEstimatedDelivery(Instant.now().plus(7, ChronoUnit.DAYS)); // Default 7 days

        order.setTrackingInfo(trackingInfo);
        order.setStatus(OrderStatus.SHIPPED);
        order.getTimestamps().setShipped(Instant.now());

        order = orderRepository.save(order);

        // Publish order shipped event
        publishOrderShippedEvent(order);

        log.info("Shipped order {} with tracking number {} via {}",
                order.getOrderNumber(), trackingNumber, carrier);

        return mapToOrderResponseDto(order);
    }

    @Override
    public OrderResponseDto markOrderDelivered(String orderId, String receivedBy) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found"));

        if (order.getStatus() != OrderStatus.SHIPPED) {
            throw new InvalidOrderStatusException("Order must be in SHIPPED status to mark as delivered");
        }

        order.setStatus(OrderStatus.DELIVERED);
        order.getTimestamps().setDelivered(Instant.now());

        if (order.getTrackingInfo() != null) {
            order.getTrackingInfo().setActualDelivery(Instant.now());
            order.getTrackingInfo().setReceivedBy(receivedBy);
        }

        order = orderRepository.save(order);

        // Publish order delivered event
        publishOrderDeliveredEvent(order);

        log.info("Marked order {} as delivered, received by {}", order.getOrderNumber(), receivedBy);

        return mapToOrderResponseDto(order);
    }

    @Override
    public OrderStatisticsDto getOrderStatistics() {
        OrderStatisticsDto stats = new OrderStatisticsDto();

        // Count orders by status
        stats.setTotalOrders(orderRepository.count());
        stats.setPendingOrders(orderRepository.countByStatus(OrderStatus.PENDING));
        stats.setConfirmedOrders(orderRepository.countByStatus(OrderStatus.CONFIRMED));
        stats.setProcessingOrders(orderRepository.countByStatus(OrderStatus.PROCESSING));
        stats.setShippedOrders(orderRepository.countByStatus(OrderStatus.SHIPPED));
        stats.setDeliveredOrders(orderRepository.countByStatus(OrderStatus.DELIVERED));
        stats.setCancelledOrders(orderRepository.countByStatus(OrderStatus.CANCELLED));
        stats.setCompletedOrders(orderRepository.countByStatus(OrderStatus.COMPLETED));

        // Calculate revenue metrics
        List<Order> allOrders = orderRepository.findAll();
        BigDecimal totalRevenue = allOrders.stream()
                .filter(order -> order.getPricing() != null)
                .map(order -> order.getPricing().getTotal())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal completedRevenue = allOrders.stream()
                .filter(order -> order.getStatus() == OrderStatus.COMPLETED ||
                        order.getStatus() == OrderStatus.DELIVERED)
                .filter(order -> order.getPricing() != null)
                .map(order -> order.getPricing().getTotal())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        stats.setTotalRevenue(totalRevenue);
        stats.setCompletedRevenue(completedRevenue);

        // Calculate average order value
        if (stats.getTotalOrders() > 0) {
            stats.setAverageOrderValue(totalRevenue.divide(
                    BigDecimal.valueOf(stats.getTotalOrders()), 2, BigDecimal.ROUND_HALF_UP).doubleValue());
        }

        // Calculate rates
        if (stats.getTotalOrders() > 0) {
            stats.setCancellationRate((double) stats.getCancelledOrders() / stats.getTotalOrders() * 100);
            stats.setFulfillmentRate((double) (stats.getDeliveredOrders() + stats.getCompletedOrders()) /
                    stats.getTotalOrders() * 100);
        }

        return stats;
    }

    @Override
    public void processPendingOrders() {
        // Find orders that have been pending for more than 1 hour
        Instant cutoffTime = Instant.now().minus(1, ChronoUnit.HOURS);
        List<Order> staleOrders = orderRepository.findStaleOrders(cutoffTime);

        for (Order order : staleOrders) {
            try {
                // Auto-cancel stale pending orders
                if (order.getStatus() == OrderStatus.PENDING) {
                    CancellationInfo cancellationInfo = new CancellationInfo();
                    cancellationInfo.setReason(CancellationReason.SYSTEM_CANCELLATION);
                    cancellationInfo.setDetails("Order automatically cancelled due to timeout");
                    cancellationInfo.setCancelledBy("SYSTEM");
                    cancellationInfo.setCancelledAt(Instant.now());

                    order.setCancellationInfo(cancellationInfo);
                    order.setStatus(OrderStatus.CANCELLED);
                    order.getTimestamps().setCancelled(Instant.now());

                    orderRepository.save(order);
                    publishOrderCancelledEvent(order);

                    log.info("Auto-cancelled stale order {}", order.getOrderNumber());
                }
            } catch (Exception e) {
                log.error("Error processing stale order {}: {}", order.getOrderNumber(), e.getMessage());
            }
        }
    }

    @Override
    public boolean validateCartForOrder(String userId) {
        try {
            Cart cart = cartRepository.findByUserId(userId).orElseThrow(ResourceNotFoundException::new);

            if (cart.getItems() == null || cart.getItems().isEmpty()) {
                return false;
            }

            List<String> productIds = cart.getItems().stream()
                    .map(CartItem::getProductId)
                    .toList();

            List<Integer> quantities = cart.getItems().stream()
                    .map(CartItem::getProductQuantity)
                    .toList();

            return inventoryReservationService.validateCartReservations(userId, productIds, quantities);

        } catch (Exception exception) {
            log.error("Error validating cart for user {}: {}", userId, exception.getMessage());
            return false;
        }
    }


    // Helper methods

    private List<OrderItem> convertCartItemsToOrderItems(List<CartItem> cartItems) {
        List<OrderItem> orderItems = new ArrayList<>();

        for (CartItem cartItem : cartItems) {
            // Get product and seller information
            Product product = productRepository.findById(cartItem.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + cartItem.getProductId()));

            Seller seller = sellerRepository.findById(product.getSellerId())
                    .orElse(null);

            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(cartItem.getProductId());
            orderItem.setProductName(cartItem.getProductName());
            orderItem.setProductDescription(cartItem.getProductDescription());
            orderItem.setQuantity(cartItem.getProductQuantity());
            orderItem.setPriceAtTime(cartItem.getDiscountedPrice()); // Use discounted price for orders
            orderItem.setTotalPrice(cartItem.getDiscountedPrice().multiply(BigDecimal.valueOf(cartItem.getProductQuantity())));
            orderItem.setSellerId(product.getSellerId());
            orderItem.setSellerName(seller != null ? seller.getCompanyName() : "Unknown Seller");

            orderItems.add(orderItem);
        }

        return orderItems;
    }

    private OrderPricing calculateOrderPricing(List<OrderItem> items) {
        BigDecimal subtotal = items.stream()
                .map(OrderItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Simple tax calculation (10%)
        BigDecimal tax = subtotal.multiply(BigDecimal.valueOf(0.10));

        // Simple shipping calculation (free over $50, otherwise $5)
        BigDecimal shipping = subtotal.compareTo(BigDecimal.valueOf(50)) >= 0 ?
                BigDecimal.ZERO : BigDecimal.valueOf(5);

        BigDecimal discount = BigDecimal.ZERO; // No discount for now

        BigDecimal total = subtotal.add(tax).add(shipping).subtract(discount);

        OrderPricing pricing = new OrderPricing();
        pricing.setSubtotal(subtotal);
        pricing.setTax(tax);
        pricing.setShipping(shipping);
        pricing.setDiscount(discount);
        pricing.setTotal(total);
        pricing.setCurrency("USD");

        return pricing;
    }

    private Order getOrderForUser(String orderId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found"));

        if (!order.getUserId().equals(user.getId())) {
            throw new RuntimeException("Access denied to this order");
        }

        return order;
    }

    private boolean canUserAccessOrder(Order order, User user) {
        // User owns the order
        if (order.getUserId().equals(user.getId())) {
            return true;
        }

        // User is a seller for any items in the order
        Seller seller = sellerRepository.findByContactEmail(user.getEmail()).orElse(null);
        if (seller != null) {
            return order.getOrderItems().stream()
                    .anyMatch(item -> item.getSellerId().equals(seller.getSellerId()));
        }

        return false;
    }

    private void updateProductStockAfterOrder(Order order) {
        for (OrderItem item : order.getOrderItems()) {
            Product product = productRepository.findById(item.getProductId()).orElse(null);
            if (product != null) {
                int newStock = product.getStockCount() - item.getQuantity();
                product.setStockCount(Math.max(0, newStock));
                productRepository.save(product);
            }
        }
    }

    private void restoreProductStockAfterCancellation(Order order) {
        for (OrderItem item : order.getOrderItems()) {
            Product product = productRepository.findById(item.getProductId()).orElse(null);
            if (product != null) {
                product.setStockCount(product.getStockCount() + item.getQuantity());
                productRepository.save(product);
            }
        }
    }

    private void validateStatusTransition(OrderStatus currentStatus, OrderStatus newStatus) {
        // Define valid transitions
        Map<OrderStatus, Set<OrderStatus>> validTransitions = Map.of(
                OrderStatus.PENDING, Set.of(OrderStatus.CONFIRMED, OrderStatus.CANCELLED),
                OrderStatus.CONFIRMED, Set.of(OrderStatus.PROCESSING, OrderStatus.CANCELLED),
                OrderStatus.PROCESSING, Set.of(OrderStatus.SHIPPED, OrderStatus.CANCELLED),
                OrderStatus.SHIPPED, Set.of(OrderStatus.DELIVERED),
                OrderStatus.DELIVERED, Set.of(OrderStatus.COMPLETED)
        );

        Set<OrderStatus> allowedStatuses = validTransitions.get(currentStatus);
        if (allowedStatuses == null || !allowedStatuses.contains(newStatus)) {
            throw new InvalidOrderStatusException(
                    String.format("Invalid status transition from %s to %s", currentStatus, newStatus));
        }
    }

    private void updateTimestampsForStatus(Order order, OrderStatus status) {
        OrderTimestamps timestamps = order.getTimestamps();
        if (timestamps == null) {
            timestamps = new OrderTimestamps();
            order.setTimestamps(timestamps);
        }

        Instant now = Instant.now();
        switch (status) {
            case CONFIRMED -> timestamps.setConfirmed(now);
            case PROCESSING -> timestamps.setProcessed(now);
            case SHIPPED -> timestamps.setShipped(now);
            case DELIVERED -> timestamps.setDelivered(now);
            case CANCELLED -> timestamps.setCancelled(now);
            case COMPLETED -> timestamps.setCompleted(now);
        }
    }

    private OrderResponseDto mapToOrderResponseDto(Order order) {
        OrderResponseDto dto = modelMapper.map(order, OrderResponseDto.class);
        dto.setTotalItemCount(order.getTotalItemCount());
        dto.setCanBeCancelled(order.canBeCancelled());
        dto.setShipped(order.isShipped());
        dto.setFinalState(order.isFinalState());
        return dto;
    }

    private OrderSummaryDto mapToOrderSummaryDto(Order order) {
        OrderSummaryDto dto = new OrderSummaryDto();
        dto.setId(order.getId());
        dto.setOrderNumber(order.getOrderNumber());
        dto.setStatus(order.getStatus());
        dto.setPaymentStatus(order.getPaymentStatus());
        dto.setTotalAmount(order.getPricing() != null ? order.getPricing().getTotal() : BigDecimal.ZERO);
        dto.setCurrency(order.getPricing() != null ? order.getPricing().getCurrency() : "USD");
        dto.setTotalItemCount(order.getTotalItemCount());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setCanBeCancelled(order.canBeCancelled());
        dto.setTrackingNumber(order.getTrackingInfo() != null ? order.getTrackingInfo().getTrackingNumber() : null);
        dto.setEstimatedDelivery(order.getTrackingInfo() != null ? order.getTrackingInfo().getEstimatedDelivery() : null);
        return dto;
    }

    // Event publishing methods

    private void publishOrderCreatedEvent(Order order) {
        try {
            Map<String, Object> eventData = Map.of(
                    "orderId", order.getId(),
                    "orderNumber", order.getOrderNumber(),
                    "itemCount", order.getTotalItemCount(),
                    "customerName", order.getCustomerName()
            );

            OrderEvent event = OrderEvent.orderCreated(
                    order.getId(),
                    order.getOrderNumber(),
                    order.getUserId(),
                    order.getCustomerEmail(),
                    order.getPricing().getTotal(),
                    order.getPricing().getCurrency(),
                    eventData
            );

            kafkaTemplate.send("order-events", order.getId(), event);
            log.debug("Published ORDER_CREATED event for order {}", order.getOrderNumber());

        } catch (Exception e) {
            log.error("Failed to publish ORDER_CREATED event for order {}: {}", order.getOrderNumber(), e.getMessage());
        }
    }

    private void publishOrderConfirmedEvent(Order order) {
        try {
            Map<String, Object> eventData = Map.of(
                    "orderId", order.getId(),
                    "orderNumber", order.getOrderNumber(),
                    "paymentTransactionId", order.getPaymentTransactionId()
            );

            OrderEvent event = OrderEvent.orderConfirmed(
                    order.getId(),
                    order.getOrderNumber(),
                    order.getUserId(),
                    order.getCustomerEmail(),
                    order.getPricing().getTotal(),
                    order.getPricing().getCurrency(),
                    eventData
            );

            kafkaTemplate.send("order-events", order.getId(), event);
            log.debug("Published ORDER_CONFIRMED event for order {}", order.getOrderNumber());

        } catch (Exception e) {
            log.error("Failed to publish ORDER_CONFIRMED event for order {}: {}", order.getOrderNumber(), e.getMessage());
        }
    }

    private void publishOrderProcessingEvent(Order order, String sellerEmail) {
        try {
            Map<String, Object> eventData = Map.of(
                    "orderId", order.getId(),
                    "orderNumber", order.getOrderNumber(),
                    "sellerEmail", sellerEmail
            );

            OrderEvent event = OrderEvent.orderProcessing(
                    order.getId(),
                    order.getOrderNumber(),
                    order.getUserId(),
                    order.getCustomerEmail(),
                    order.getPricing().getTotal(),
                    order.getPricing().getCurrency(),
                    eventData
            );

            kafkaTemplate.send("order-events", order.getId(), event);
            log.debug("Published ORDER_PROCESSING event for order {}", order.getOrderNumber());

        } catch (Exception e) {
            log.error("Failed to publish ORDER_PROCESSING event for order {}: {}", order.getOrderNumber(), e.getMessage());
        }
    }

    private void publishOrderCancelledEvent(Order order) {
        try {
            Map<String, Object> eventData = new HashMap<>();
            eventData.put("orderId", order.getId());
            eventData.put("orderNumber", order.getOrderNumber());

            if (order.getCancellationInfo() != null) {
                eventData.put("reason", order.getCancellationInfo().getReason());
                eventData.put("cancelledBy", order.getCancellationInfo().getCancelledBy());
                eventData.put("refundAmount", order.getCancellationInfo().getRefundAmount());
            }

            OrderEvent event = OrderEvent.orderCancelled(
                    order.getId(),
                    order.getOrderNumber(),
                    order.getUserId(),
                    order.getCustomerEmail(),
                    order.getPricing().getTotal(),
                    order.getPricing().getCurrency(),
                    eventData
            );

            kafkaTemplate.send("order-events", order.getId(), event);
            log.debug("Published ORDER_CANCELLED event for order {}", order.getOrderNumber());

        } catch (Exception e) {
            log.error("Failed to publish ORDER_CANCELLED event for order {}: {}", order.getOrderNumber(), e.getMessage());
        }
    }

    private void publishOrderShippedEvent(Order order) {
        try {
            Map<String, Object> eventData = new HashMap<>();
            eventData.put("orderId", order.getId());
            eventData.put("orderNumber", order.getOrderNumber());

            if (order.getTrackingInfo() != null) {
                eventData.put("trackingNumber", order.getTrackingInfo().getTrackingNumber());
                eventData.put("carrier", order.getTrackingInfo().getCarrier());
                eventData.put("estimatedDelivery", order.getTrackingInfo().getEstimatedDelivery());
            }

            OrderEvent event = OrderEvent.orderShipped(
                    order.getId(),
                    order.getOrderNumber(),
                    order.getUserId(),
                    order.getCustomerEmail(),
                    order.getPricing().getTotal(),
                    order.getPricing().getCurrency(),
                    eventData
            );

            kafkaTemplate.send("order-events", order.getId(), event);
            log.debug("Published ORDER_SHIPPED event for order {}", order.getOrderNumber());

        } catch (Exception e) {
            log.error("Failed to publish ORDER_SHIPPED event for order {}: {}", order.getOrderNumber(), e.getMessage());
        }
    }

    private void publishOrderDeliveredEvent(Order order) {
        try {
            Map<String, Object> eventData = new HashMap<>();
            eventData.put("orderId", order.getId());
            eventData.put("orderNumber", order.getOrderNumber());

            if (order.getTrackingInfo() != null) {
                eventData.put("deliveredAt", order.getTrackingInfo().getActualDelivery());
                eventData.put("receivedBy", order.getTrackingInfo().getReceivedBy());
            }

            OrderEvent event = OrderEvent.orderDelivered(
                    order.getId(),
                    order.getOrderNumber(),
                    order.getUserId(),
                    order.getCustomerEmail(),
                    order.getPricing().getTotal(),
                    order.getPricing().getCurrency(),
                    eventData
            );

            kafkaTemplate.send("order-events", order.getId(), event);
            log.debug("Published ORDER_DELIVERED event for order {}", order.getOrderNumber());

        } catch (Exception e) {
            log.error("Failed to publish ORDER_DELIVERED event for order {}: {}", order.getOrderNumber(), e.getMessage());
        }
    }

    private void publishStatusChangeEvent(Order order, OrderStatus previousStatus, String updatedBy) {
        // Publish specific events based on the new status
        switch (order.getStatus()) {
            case CONFIRMED -> publishOrderConfirmedEvent(order);
            case CANCELLED -> publishOrderCancelledEvent(order);
            case SHIPPED -> publishOrderShippedEvent(order);
            case DELIVERED -> publishOrderDeliveredEvent(order);
            default -> log.debug("No specific event handler for status change to {}", order.getStatus());
        }
    }

    /**
     * Validate payment transaction ID format
     * For now, just basic validation - later integrate with actual payment gateway
     */
    private void validatePaymentTransactionId(String paymentTransactionId) {
        if (paymentTransactionId == null || paymentTransactionId.trim().isEmpty()) {
            throw new IllegalArgumentException("Payment transaction ID cannot be empty");
        }
        
        // Basic format validation - should be alphanumeric and have minimum length
        if (!paymentTransactionId.matches("^[A-Za-z0-9_-]{6,50}$")) {
            throw new IllegalArgumentException("Invalid payment transaction ID format. Must be 6-50 alphanumeric characters.");
        }
        
        log.info("Payment transaction ID validated: {}", paymentTransactionId);
    }

    /**
     * Validate inventory availability for all cart items
     */
    private void validateInventoryForOrder(List<OrderItem> orderItems) {
        for (OrderItem item : orderItems) {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + item.getProductId()));
            
            if (product.getStockCount() < item.getQuantity()) {
                throw new IllegalStateException(
                    String.format("Insufficient stock for product %s. Available: %d, Requested: %d", 
                                product.getName(), product.getStockCount(), item.getQuantity()));
            }
            
            log.debug("Inventory validated for product {}: {} units available", 
                     product.getName(), product.getStockCount());
        }
    }
}
