package shopeazy.com.ecommerce_app.shopping_cart.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import shopeazy.com.ecommerce_app.shopping_cart.model.Cart;

import java.util.Optional;

public interface CartRepository extends MongoRepository<Cart, String> {
    Optional<Cart> findByUserId(String id);
}
