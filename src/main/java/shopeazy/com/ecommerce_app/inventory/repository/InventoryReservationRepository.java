package shopeazy.com.ecommerce_app.inventory.repository;


import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import shopeazy.com.ecommerce_app.inventory.model.InventoryReservation;

import java.util.List;
import java.util.Optional;

/**
 * Repository for inventory reservations stored in Redis.
 * Handles cart-based inventory reservations with automatic expiration.
 */
@Repository
public interface InventoryReservationRepository extends CrudRepository<InventoryReservation, String> {

    /**
     * Find all reservations for a specific user
     */
    List<InventoryReservation> findByUserId(String userId);

    /**
     * Find all reservations for a specific product
     */
    List<InventoryReservation> findByProductId(String productId);

    /**
     * Find reservation for specific user and product
     */
    Optional<InventoryReservation> findByUserIdAndProductId(String userId, String productId);
}
