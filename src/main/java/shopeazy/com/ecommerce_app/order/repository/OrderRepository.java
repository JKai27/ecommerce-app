package shopeazy.com.ecommerce_app.order.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import shopeazy.com.ecommerce_app.order.enums.OrderStatus;
import shopeazy.com.ecommerce_app.order.enums.PaymentStatus;
import shopeazy.com.ecommerce_app.order.model.Order;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends MongoRepository<Order, String> {
    Optional<Order> findByOrderNumber(String orderNumber);

    /**
     * Find all order for a specific user and sort by the CreatedAt field in descending order
     */
    Page<Order> findByUserIdOrderByCreatedAtDesc(String userId, Pageable pageable);

    /**
     * Find order for a specific user with status filter
     */
    Page<Order> findByUserIdAndStatusOrderByCreatedAtDesc(String userId, OrderStatus status, Pageable pageable);

    /**
     * Find order by status
     */
    Page<Order> findByStatusOrderByCreatedAtDesc(OrderStatus status, Pageable pageable);

    /**
     * Find order by payment status
     */
    List<Order> findByPaymentStatus(PaymentStatus paymentStatus);

    /**
     * Find order that need status update (stuck in processing states)
     */
    @Query("{'status': {$in: ['PENDING', 'CONFIRMED','PROCESSING']},'createdAt': {$lt: ?0}}")
    List<Order> findStaleOrders(Instant cutOffTime);

    /**
     * Find orders for a seller (contains products from this seller)
     */
    @Query("{ 'items.sellerId': ?0 }")
    Page<Order> findOrdersContainingSellerProducts(String sellerId, Pageable pageable);

    /**
     * Find orders for a seller with status filter
     */
    @Query("{ 'items.sellerId': ?0, 'status': ?1 }")
    Page<Order> findOrdersContainingSellerProductsWithStatus(String sellerId, OrderStatus status, Pageable pageable);

    /**
     * Find orders created within date range
     */
    List<Order> findByCreatedAtBetween(Instant startDate, Instant endDate);



    /**
     * Count order by status
     */
    long countByStatus(OrderStatus status);

    /**
     * Count order by user and status
     */
    long countByUserIdAndStatus(String userId, OrderStatus status);

    /**
     * Find order by tracking number
     */
    @Query("{ 'trackingInfo.trackingNumber': ?0 }")
    Optional<Order> findByTrackingNumber(String trackingNumber);

    /**
     * Find order with pending payments older than specified time
     */
    @Query("{ 'paymentStatus': 'PENDING', 'createdAt': { $lt: ?0 } }")
    List<Order> findPendingPaymentsOlderThan(Instant cutoffTime);
}
