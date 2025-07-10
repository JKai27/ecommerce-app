package shopeazy.com.ecommerce_app.repository;

import jakarta.validation.constraints.NotBlank;
import org.springframework.data.mongodb.repository.MongoRepository;
import shopeazy.com.ecommerce_app.model.document.Product;

import java.util.List;

public interface ProductRepository extends MongoRepository<Product, String> {
    long countBySellerId(String sellerId);

    boolean existsByNameAndSellerId(@NotBlank(message = "Product name is required") String name, @NotBlank(message = "Seller ID is required") String sellerId);

    List<Product> findBySellerId(String sellerId);

    void deleteBySellerId(String sellerId);
}
// PaginationandSortingRepository