package shopeazy.com.ecommerce_app.order.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import shopeazy.com.ecommerce_app.order.dto.CreateOrderRequest;
import shopeazy.com.ecommerce_app.order.dto.OrderResponseDto;
import shopeazy.com.ecommerce_app.order.dto.OrderStatisticsDto;
import shopeazy.com.ecommerce_app.order.dto.OrderSummaryDto;
import shopeazy.com.ecommerce_app.order.dto.ProcessOrderRequest;
import shopeazy.com.ecommerce_app.order.enums.OrderStatus;


/**
 * Service interface for order management operations.
 * Handles the complete order lifecycle from creation to completion.
 */
public interface OrderService {

    /**
     * Create a new order from user's shopping cart
     */
    OrderResponseDto createOrderFromCart(CreateOrderRequest request, String userEmail);

    /**
     * Get order by ID
     */
    OrderResponseDto getOrderById(String orderId, String userEmail);

    /**
     * Get order by order number
     */
    OrderResponseDto getOrderByOrderNumber(String orderNumber, String userEmail);

    /**
     * Get all orders for a user with pagination
     */
    Page<OrderSummaryDto> getUserOrders(String userEmail, Pageable pageable);

    /**
     * Get user orders filtered by status
     */
    Page<OrderSummaryDto> getUserOrdersByStatus(String userEmail, OrderStatus status, Pageable pageable);

    /**
     * Get orders for a seller (containing their products)
     */
    Page<OrderSummaryDto> getSellerOrders(String sellerEmail, Pageable pageable);

    /**
     * Get seller orders filtered by status
     */
    Page<OrderSummaryDto> getSellerOrdersByStatus(String sellerEmail, OrderStatus status, Pageable pageable);

    /**
     * Confirm order (move from PENDING to CONFIRMED)
     */
    OrderResponseDto confirmOrder(String orderId, String userEmail);

    /**
     * Mark order as processing (move from CONFIRMED to PROCESSING)
     */
    OrderResponseDto processOrder(String orderId, ProcessOrderRequest request, String sellerEmail);

    /**
     * Cancel an order
     */
    OrderResponseDto cancelOrder(String orderId, String userEmail, String reason);

    /**
     * Update order status (for sellers/admins)
     */
    OrderResponseDto updateOrderStatus(String orderId, OrderStatus newStatus, String updatedBy);

    /**
     * Mark order as shipped with tracking information
     */
    OrderResponseDto shipOrder(String orderId, String trackingNumber, String carrier, String sellerEmail);

    /**
     * Mark order as delivered
     */
    OrderResponseDto markOrderDelivered(String orderId, String receivedBy);

    /**
     * Get order statistics for admin dashboard
     */
    OrderStatisticsDto getOrderStatistics();

    /**
     * Process pending orders (cleanup stale orders)
     */
    void processPendingOrders();

    /**
     * Validate order can be created from cart
     */
    boolean validateCartForOrder(String userId);
}