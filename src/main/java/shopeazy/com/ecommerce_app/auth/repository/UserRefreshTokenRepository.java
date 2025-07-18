package shopeazy.com.ecommerce_app.auth.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import shopeazy.com.ecommerce_app.auth.model.UserRefreshToken;

import java.util.Optional;

public interface UserRefreshTokenRepository extends MongoRepository<UserRefreshToken, String> {
    Optional<UserRefreshToken> findByToken(String token);

    void deleteByUserId(String id);
}
