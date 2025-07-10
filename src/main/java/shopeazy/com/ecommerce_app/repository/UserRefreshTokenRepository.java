package shopeazy.com.ecommerce_app.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import shopeazy.com.ecommerce_app.model.document.UserRefreshToken;

import java.util.Optional;

public interface UserRefreshTokenRepository extends MongoRepository<UserRefreshToken, String> {
    Optional<UserRefreshToken> findByToken(String token);

    void deleteByUserId(String id);
}
