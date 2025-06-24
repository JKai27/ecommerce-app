package shopeazy.com.ecommerceapp.repository;

import jakarta.validation.constraints.NotBlank;
import org.springframework.data.mongodb.repository.MongoRepository;
import shopeazy.com.ecommerceapp.model.document.Product;

public interface ProductRepository extends MongoRepository<Product, String> {
    long countBySellerId(String sellerId);

    boolean existsByNameAndSellerId(@NotBlank(message = "Product name is required") String name, @NotBlank(message = "Seller ID is required") String sellerId);
}
