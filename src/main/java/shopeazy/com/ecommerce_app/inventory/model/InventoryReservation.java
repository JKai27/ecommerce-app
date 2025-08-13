package shopeazy.com.ecommerce_app.inventory.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.time.Instant;

/**
 * Redis entity for tracking inventory reservations.
 * Used to reserve stock when items are added to cart with automatic expiration.
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@RedisHash("inventory_reservation")
public class InventoryReservation {

    @Id
    private String id; // Format: userId:productId

    /**
     * User who made the reservation
     */
    private String userId;

    /**
     * Product being reserved
     */
    private String productId;


    /**
     * Quantity reserved
     */
    private Integer quantity;

    /**
     * When the reservation was created
     */
    private Instant createdAt;

    /**
     * When the reservation expires (in seconds from creation)
     */
    @TimeToLive
    private Long ttlSeconds;

    /**
     * Type of reservation (CART, ORDER)
     */
    private String reservationType;

    /**
     * Additional context (cart ID, order ID, etc.)
     */
    private String context;

    /**
     * Create a cart Reservation with default TTL
     */
    public static InventoryReservation createCartReservation(String userId, String productId, Integer quantity, Long ttlSeconds) {
        InventoryReservation inventoryReservation = new InventoryReservation();
        inventoryReservation.setId(userId + ":" + productId);
        inventoryReservation.setUserId(userId);
        inventoryReservation.setProductId(productId);
        inventoryReservation.setQuantity(quantity);
        inventoryReservation.setCreatedAt(Instant.now());
        inventoryReservation.setTtlSeconds(ttlSeconds);
        inventoryReservation.setReservationType("CART");
        return inventoryReservation;

    }
}
