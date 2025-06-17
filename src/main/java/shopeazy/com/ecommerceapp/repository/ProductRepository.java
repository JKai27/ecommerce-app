package shopeazy.com.ecommerceapp.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import shopeazy.com.ecommerceapp.model.document.Product;

public interface ProductRepository extends MongoRepository<Product, String> {
    long countBySellerId(String sellerId);
}
