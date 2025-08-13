package shopeazy.com.ecommerce_app.inventory.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import shopeazy.com.ecommerce_app.common.exception.ResourceNotFoundException;
import shopeazy.com.ecommerce_app.inventory.model.InventoryReservation;
import shopeazy.com.ecommerce_app.inventory.repository.InventoryReservationRepository;
import shopeazy.com.ecommerce_app.product.model.Product;
import shopeazy.com.ecommerce_app.product.repository.ProductRepository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryReservationService {
    private final InventoryReservationRepository reservationRepository;
    private final ProductRepository productRepository;

    @Value("${app.order.reservation-timeout-minutes}")
    private Integer reservationTimeoutMinutes;

    /**
     * Reserve inventory for cart items
     */
    public boolean reserveInventory(String userId, String productId, Integer quantity) {
        try {
            productRepository.findById(productId).orElseThrow(ResourceNotFoundException::new);

            int availableStock = getAvailableStock(productId);

            // Check for existing reservation for this user-product combination
            Optional<InventoryReservation> existingReservation =
                    reservationRepository.findByUserIdAndProductId(userId, productId);

            int currentReserved = existingReservation.map(InventoryReservation::getQuantity).orElse(0);
            int additionalNeeded = quantity - currentReserved;
            if (additionalNeeded > availableStock) {
                log.warn("Insufficient stock for product {}. Available: {}, Requested additional: {}",
                        productId, availableStock, additionalNeeded);
                return false;
            }

            // Create or update reservation
            InventoryReservation reservation = existingReservation.orElse(
                    InventoryReservation.createCartReservation(userId, productId, 0, (long) (reservationTimeoutMinutes * 60))
            );

            reservation.setQuantity(quantity);
            reservation.setTtlSeconds((long) (reservationTimeoutMinutes * 60));

            reservationRepository.save(reservation);

            log.info("Reserved {} units of product {} for user {} (expires in {} minutes)",
                    quantity, productId, userId, reservationTimeoutMinutes);

            return true;

        } catch (Exception e) {
            log.error("Error reserving inventory for user {} and product {}: {}", userId, productId, e.getMessage());
            return false;
        }
    }

    public int getAvailableStock(String productId) {
        try {
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

            int totalStock = product.getStockCount();
            int reservedStock = getTotalReservedStock(productId);

            return Math.max(0, totalStock - reservedStock);
        } catch (Exception e) {
            log.error("Error calculating available stock for product {}: {}", productId, e.getMessage());
            return 0;
        }
    }

    /**
     * Get total reserved stock for a product
     */
    public int getTotalReservedStock(String productId) {
        try {
            List<InventoryReservation> reservations = reservationRepository.findByProductId(productId);
            return reservations.stream()
                    .mapToInt(InventoryReservation::getQuantity)
                    .sum();
        } catch (Exception e) {
            log.error("Error calculating reserved stock for product {}: {}", productId, e.getMessage());
            return 0;
        }
    }

    /**
     * Release reservation for specific user and product
     */
    public void releaseReservation(String userId, String productId) {
        try {
            String reservationId = userId + ":" + productId;
            reservationRepository.deleteById(reservationId);
            log.info("Released reservation for user {} and product {}", userId, productId);
        } catch (Exception e) {
            log.error("Error releasing reservation for user {} and product {}: {}", userId, productId, e.getMessage());
        }
    }

    /**
     * Release all reservations for a user (when cart is cleared or order is placed)
     */
    public void releaseAllUserReservations(String userId) {
        try {
            List<InventoryReservation> userReservations = reservationRepository.findByUserId(userId);
            for (InventoryReservation reservation : userReservations) {
                reservationRepository.delete(reservation);
            }
            log.info("Released {} reservations for user {}", userReservations.size(), userId);
        } catch (Exception e) {
            log.error("Error releasing all reservations for user {}: {}", userId, e.getMessage());
        }
    }
}
