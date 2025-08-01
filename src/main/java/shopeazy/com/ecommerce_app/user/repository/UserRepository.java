package shopeazy.com.ecommerce_app.user.repository;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.mongodb.repository.MongoRepository;
import shopeazy.com.ecommerce_app.user.model.User;

import java.util.Optional;


public interface UserRepository extends MongoRepository<User, String> {
    boolean existsByEmail(@NotBlank @Email String email);
    Optional<User> findByEmail(String email);

    Optional<User> getUserById(String id);
}
