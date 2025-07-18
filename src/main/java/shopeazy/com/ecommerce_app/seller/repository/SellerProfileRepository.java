package shopeazy.com.ecommerce_app.seller.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import shopeazy.com.ecommerce_app.seller.model.Seller;

import java.util.List;
import java.util.Optional;

public interface SellerProfileRepository extends MongoRepository<Seller, String> {
    Optional<Seller> findByContactEmail(String email);

    boolean existsByContactEmail(String userEmail);

    List<Seller> findByCompanyName(String companyName);

}
