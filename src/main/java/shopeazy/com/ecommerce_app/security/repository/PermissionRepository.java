package shopeazy.com.ecommerce_app.security.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import shopeazy.com.ecommerce_app.security.model.Permission;

import java.util.List;
import java.util.Optional;

public interface PermissionRepository extends MongoRepository<Permission, String> {
    Optional<Permission> findByName(String read);

    // Find multiple permissions by their names
    List<Permission> findAllByNameIn(List<String> permissionNames);
}
