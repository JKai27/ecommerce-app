package shopeazy.com.ecommerce_app.security.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import shopeazy.com.ecommerce_app.security.model.Permission;
import shopeazy.com.ecommerce_app.security.repository.PermissionRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PermissionService {
    private final PermissionRepository permissionRepository;

    // Get Permission by name
    public Permission getPermissionByName(String permissionName) {
        return permissionRepository.findByName(permissionName)
                .orElseThrow(() -> new RuntimeException("Permission not found: " + permissionName));
    }

    // Get all Permissions by names
    public List<Permission> getPermissionsByNames(List<String> permissionNames) {
        return permissionRepository.findAllByNameIn(permissionNames);
    }
}
