package shopeazy.com.ecommerceapp.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import shopeazy.com.ecommerceapp.model.Permission;
import shopeazy.com.ecommerceapp.model.Role;

import java.util.List;
import java.util.Optional;

public interface RoleRepository extends MongoRepository<Role, String> {
    Optional<Role> findByName(String user);

    // Find multiple permissions by their names
    List<Role> findAllByNameIn(List<String> permissionNames);
}
