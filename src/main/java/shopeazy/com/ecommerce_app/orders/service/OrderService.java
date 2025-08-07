package shopeazy.com.ecommerce_app.orders.service;

import shopeazy.com.ecommerce_app.orders.dto.CreateOrderRequest;
import shopeazy.com.ecommerce_app.orders.dto.OrderResponseDto;

/**
 * Service interface for order management operations.
 * Handles the complete order lifecycle from creation to completion.
 */
public interface OrderService {
    /**
     * Create a new order from user's shopping cart
     */
    OrderResponseDto createOrderFromCart(CreateOrderRequest request, String userEmail);
}
