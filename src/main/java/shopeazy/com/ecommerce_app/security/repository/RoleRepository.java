package shopeazy.com.ecommerce_app.security.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import shopeazy.com.ecommerce_app.security.model.Role;

import java.util.List;
import java.util.Optional;

public interface RoleRepository extends MongoRepository<Role, String> {
    Optional<Role> findByName(String roleName);

    Optional<List<Role>> findByNameIn(List<String> roleNames);

    List<Role> findAllByNameIn(List<String> roles);
}
