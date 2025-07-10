package shopeazy.com.ecommerce_app.service.implementation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import shopeazy.com.ecommerce_app.model.document.Permission;
import shopeazy.com.ecommerce_app.model.document.User;
import shopeazy.com.ecommerce_app.repository.RoleRepository;

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