package shopeazy.com.ecommerceapp.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import shopeazy.com.ecommerceapp.model.document.UserRefreshToken;

import java.util.Optional;

public interface UserRefreshTokenRepository extends MongoRepository<UserRefreshToken, String> {
    Optional<UserRefreshToken> findByToken(String token);

    void deleteByUserId(String id);
}
