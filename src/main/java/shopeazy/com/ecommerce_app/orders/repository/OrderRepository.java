package shopeazy.com.ecommerce_app.orders.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.aggregation.BooleanOperators;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import shopeazy.com.ecommerce_app.orders.enums.OrderStatus;
import shopeazy.com.ecommerce_app.orders.enums.PaymentStatus;
import shopeazy.com.ecommerce_app.orders.model.Order;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends MongoRepository<Order, String> {
    Optional<Order> findByOrderNumber(String orderNumber);

    /**
     * Find all orders for a specific user and sort by the CreatedAt field in descending order
     */
    Page<Order> findByUserIdOrderByCreatedAtDesc(String userId, Pageable pageable);

    /**
     * Find orders for a specific user with status filter
     */
    Page<Order> findByUserIdAndStatusOrderByCreatedAtDesc(String userId, OrderStatus status, Pageable pageable);

    /**
     * Find orders by status
     */
    Page<Order> findByStatusOrderByCreatedAtDesc(OrderStatus status, Pageable pageable);

    /**
     * Find orders by payment status
     */
    List<Order> findByPaymentStatus(PaymentStatus paymentStatus);

    /**
     * Find orders that need status update (stuck in processing states)
     */
    @Query("{'status': {$in: ['PENDING', 'CONFIRMED','PROCESSING']},'createdAt': {$lt: ?0}}")
    List<Order> findStaleOrders(Instant cutOffTime);


    /**
     * Count orders by status
     */
    long countByStatus(OrderStatus status);

    /**
     * Count orders by user and status
     */
    long countByUserIdAndStatus(String userId, OrderStatus status);

    /**
     * Find orders by tracking number
     */
    @Query("{ 'trackingInfo.trackingNumber': ?0 }")
    Optional<Order> findByTrackingNumber(String trackingNumber);

    /**
     * Find orders with pending payments older than specified time
     */
    @Query("{ 'paymentStatus': 'PENDING', 'createdAt': { $lt: ?0 } }")
    List<Order> findPendingPaymentsOlderThan(Instant cutoffTime);
}
