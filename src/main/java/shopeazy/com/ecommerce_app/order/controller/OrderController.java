package shopeazy.com.ecommerce_app.order.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import shopeazy.com.ecommerce_app.common.dto.ApiResponse;
import shopeazy.com.ecommerce_app.order.dto.*;
import shopeazy.com.ecommerce_app.order.dto.ProcessOrderRequest;
import shopeazy.com.ecommerce_app.order.enums.OrderStatus;
import shopeazy.com.ecommerce_app.order.service.OrderService;

import java.time.Instant;

/**
 * REST Controller for order management operations.
 * Provides endpoints for order lifecycle management and tracking.
 */
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Slf4j
public class OrderController {
    
    private final OrderService orderService;
    
    /**
     * Create a new order from user's shopping cart
     */
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<OrderResponseDto>> createOrder(
            @Valid @RequestBody CreateOrderRequest request,
            Authentication authentication) {
        
        try {
            String userEmail = authentication.getName();
            log.info("Creating order for user: {}", userEmail);
            
            OrderResponseDto order = orderService.createOrderFromCart(request, userEmail);
            
            ApiResponse<OrderResponseDto> response = new ApiResponse<>(
                    true,
                    "Order created successfully",
                    order,
                    Instant.now()
            );
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (Exception e) {
            log.error("Error creating order: {}", e.getMessage(), e);
            
            ApiResponse<OrderResponseDto> response = new ApiResponse<>(
                    false,
                    "Failed to create order: " + e.getMessage(),
                    null,
                    Instant.now()
            );
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    
    /**
     * Get all orders for the authenticated user
     */
    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<Page<OrderSummaryDto>>> getUserOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) OrderStatus status,
            Authentication authentication) {
        
        try {
            String userEmail = authentication.getName();
            Pageable pageable = PageRequest.of(page, size);
            
            Page<OrderSummaryDto> orders;
            if (status != null) {
                orders = orderService.getUserOrdersByStatus(userEmail, status, pageable);
            } else {
                orders = orderService.getUserOrders(userEmail, pageable);
            }
            
            ApiResponse<Page<OrderSummaryDto>> response = new ApiResponse<>(
                    true,
                    "Orders retrieved successfully",
                    orders,
                    Instant.now()
            );
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error retrieving orders: {}", e.getMessage(), e);
            
            ApiResponse<Page<OrderSummaryDto>> response = new ApiResponse<>(
                    false,
                    "Failed to retrieve orders: " + e.getMessage(),
                    null,
                    Instant.now()
            );
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Get order by ID
     */
    @GetMapping("/{orderId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<OrderResponseDto>> getOrderById(
            @PathVariable String orderId,
            Authentication authentication) {
        
        try {
            String userEmail = authentication.getName();
            log.info("Retrieving order : {} for user {} ", orderId, userEmail);
            
            OrderResponseDto order = orderService.getOrderById(orderId, userEmail);
            
            ApiResponse<OrderResponseDto> response = new ApiResponse<>(
                    true,
                    "Order retrieved successfully",
                    order,
                    Instant.now()
            );
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error retrieving order {}: {}", orderId, e.getMessage(), e);
            
            ApiResponse<OrderResponseDto> response = new ApiResponse<>(
                    false,
                    "Failed to retrieve order: " + e.getMessage(),
                    null,
                    Instant.now()
            );
            
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
    
    /**
     * Get order by order number
     */
    @GetMapping("/number/{orderNumber}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<OrderResponseDto>> getOrderByNumber(
            @PathVariable String orderNumber,
            Authentication authentication) {
        
        try {
            String userEmail = authentication.getName();
            log.info("Retrieving order {} for user {}", orderNumber, userEmail);
            
            OrderResponseDto order = orderService.getOrderByOrderNumber(orderNumber, userEmail);
            
            ApiResponse<OrderResponseDto> response = new ApiResponse<>(
                    true,
                    "Order retrieved successfully",
                    order,
                    Instant.now()
            );
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error retrieving order {}: {}", orderNumber, e.getMessage(), e);
            
            ApiResponse<OrderResponseDto> response = new ApiResponse<>(
                    false,
                    "Failed to retrieve order: " + e.getMessage(),
                    null,
                    Instant.now()
            );
            
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
    
    /**
     * Confirm order (move from PENDING to CONFIRMED)
     */
    @PutMapping("/{orderId}/confirm")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<OrderResponseDto>> confirmOrder(
            @PathVariable String orderId,
            Authentication authentication) {
        
        try {
            String userEmail = authentication.getName();
            log.info("Confirming order {} for user {}", orderId, userEmail);
            
            OrderResponseDto order = orderService.confirmOrder(orderId, userEmail);
            
            ApiResponse<OrderResponseDto> response = new ApiResponse<>(
                    true,
                    "Order confirmed successfully",
                    order,
                    Instant.now()
            );
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error confirming order {}: {}", orderId, e.getMessage(), e);
            
            ApiResponse<OrderResponseDto> response = new ApiResponse<>(
                    false,
                    "Failed to confirm order: " + e.getMessage(),
                    null,
                    Instant.now()
            );
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    
    /**
     * Cancel an order
     */
    @PutMapping("/{orderId}/cancel")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<OrderResponseDto>> cancelOrder(
            @PathVariable String orderId,
            @RequestBody CancelOrderRequest request,
            Authentication authentication) {
        
        try {
            String userEmail = authentication.getName();
            log.info("Cancelling order {} for user {}", orderId, userEmail);
            
            OrderResponseDto order = orderService.cancelOrder(orderId, userEmail, request.getReason());
            
            ApiResponse<OrderResponseDto> response = new ApiResponse<>(
                    true,
                    "Order cancelled successfully",
                    order,
                    Instant.now()
            );
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error cancelling order {}: {}", orderId, e.getMessage(), e);
            
            ApiResponse<OrderResponseDto> response = new ApiResponse<>(
                    false,
                    "Failed to cancel order: " + e.getMessage(),
                    null,
                    Instant.now()
            );
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    
    /**
     * Ship an order (for sellers)
     */
    @PutMapping("/{orderId}/ship")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ApiResponse<OrderResponseDto>> shipOrder(
            @PathVariable String orderId,
            @Valid @RequestBody ShipOrderRequest request,
            Authentication authentication) {
        
        try {
            String sellerEmail = authentication.getName();
            log.info("Shipping order {} by seller {}", orderId, sellerEmail);
            
            OrderResponseDto order = orderService.shipOrder(
                    orderId, 
                    request.getTrackingNumber(), 
                    request.getCarrier(), 
                    sellerEmail
            );
            
            ApiResponse<OrderResponseDto> response = new ApiResponse<>(
                    true,
                    "Order shipped successfully",
                    order,
                    Instant.now()
            );
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error shipping order {}: {}", orderId, e.getMessage(), e);
            
            ApiResponse<OrderResponseDto> response = new ApiResponse<>(
                    false,
                    "Failed to ship order: " + e.getMessage(),
                    null,
                    Instant.now()
            );
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    
    /**
     * Mark order as being processed
     */
    @PutMapping("/{orderId}/process")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ApiResponse<OrderResponseDto>> processOrder(
            @PathVariable String orderId,
            @Valid @RequestBody ProcessOrderRequest request,
            Authentication authentication) {
        
        try {
            String sellerEmail = authentication.getName();
            log.info("Processing order {} by seller {}", orderId, sellerEmail);
            
            OrderResponseDto order = orderService.processOrder(orderId, request, sellerEmail);
            
            ApiResponse<OrderResponseDto> response = new ApiResponse<>(
                    true,
                    "Order marked as processing successfully",
                    order,
                    Instant.now()
            );
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error processing order {}: {}", orderId, e.getMessage(), e);
            
            ApiResponse<OrderResponseDto> response = new ApiResponse<>(
                    false,
                    "Failed to process order: " + e.getMessage(),
                    null,
                    Instant.now()
            );
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    /**
     * Mark order as delivered
     */
    @PutMapping("/{orderId}/deliver")
    @PreAuthorize("hasRole('SELLER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<OrderResponseDto>> markOrderDelivered(
            @PathVariable String orderId,
            @RequestBody DeliverOrderRequest request) {
        
        try {
            log.info("Marking order {} as delivered", orderId);
            
            OrderResponseDto order = orderService.markOrderDelivered(orderId, request.getReceivedBy());
            
            ApiResponse<OrderResponseDto> response = new ApiResponse<>(
                    true,
                    "Order marked as delivered successfully",
                    order,
                    Instant.now()
            );
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error marking order {} as delivered: {}", orderId, e.getMessage(), e);
            
            ApiResponse<OrderResponseDto> response = new ApiResponse<>(
                    false,
                    "Failed to mark order as delivered: " + e.getMessage(),
                    null,
                    Instant.now()
            );
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    
    /**
     * Get orders for seller (containing their products)
     */
    @GetMapping("/seller")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ApiResponse<Page<OrderSummaryDto>>> getSellerOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) OrderStatus status,
            Authentication authentication) {
        
        try {
            String sellerEmail = authentication.getName();
            Pageable pageable = PageRequest.of(page, size);
            
            Page<OrderSummaryDto> orders;
            if (status != null) {
                orders = orderService.getSellerOrdersByStatus(sellerEmail, status, pageable);
            } else {
                orders = orderService.getSellerOrders(sellerEmail, pageable);
            }
            
            ApiResponse<Page<OrderSummaryDto>> response = new ApiResponse<>(
                    true,
                    "Seller orders retrieved successfully",
                    orders,
                    Instant.now()
            );
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error retrieving seller orders: {}", e.getMessage(), e);
            
            ApiResponse<Page<OrderSummaryDto>> response = new ApiResponse<>(
                    false,
                    "Failed to retrieve seller orders: " + e.getMessage(),
                    null,
                    Instant.now()
            );
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Update order status (for admins)
     */
    @PutMapping("/{orderId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<OrderResponseDto>> updateOrderStatus(
            @PathVariable String orderId,
            @Valid @RequestBody UpdateOrderStatusRequest request,
            Authentication authentication) {
        
        try {
            String updatedBy = authentication.getName();
            log.info("Updating order {} status to {} by {}", orderId, request.getNewStatus(), updatedBy);
            
            OrderResponseDto order = orderService.updateOrderStatus(orderId, request.getNewStatus(), updatedBy);
            
            ApiResponse<OrderResponseDto> response = new ApiResponse<>(
                    true,
                    "Order status updated successfully",
                    order,
                    Instant.now()
            );
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error updating order {} status: {}", orderId, e.getMessage(), e);
            
            ApiResponse<OrderResponseDto> response = new ApiResponse<>(
                    false,
                    "Failed to update order status: " + e.getMessage(),
                    null,
                    Instant.now()
            );
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    
    /**
     * Get order statistics (for admins)
     */
    @GetMapping("/statistics")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<OrderStatisticsDto>> getOrderStatistics() {
        
        try {
            log.info("Retrieving order statistics");
            
            OrderStatisticsDto statistics = orderService.getOrderStatistics();
            
            ApiResponse<OrderStatisticsDto> response = new ApiResponse<>(
                    true,
                    "Order statistics retrieved successfully",
                    statistics,
                    Instant.now()
            );
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error retrieving order statistics: {}", e.getMessage(), e);
            
            ApiResponse<OrderStatisticsDto> response = new ApiResponse<>(
                    false,
                    "Failed to retrieve order statistics: " + e.getMessage(),
                    null,
                    Instant.now()
            );
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}