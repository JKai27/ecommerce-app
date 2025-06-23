package shopeazy.com.ecommerceapp.service.serviceImplementation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import shopeazy.com.ecommerceapp.model.document.Permission;
import shopeazy.com.ecommerceapp.model.document.User;
import shopeazy.com.ecommerceapp.repository.RoleRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PermissionResolverService {
    private final RoleRepository roleRepository;

    public Set<String> resolvePermissions(User user) {
        return user.getRoles().stream()
                .map(roleRepository::findByName)
                .filter(Optional::isPresent)
                .flatMap(role -> role.get().getPermissions().stream())
                .map(Permission::getName)
                .collect(Collectors.toSet());
    }
}