package shopeazy.com.ecommerce_app.service.implementation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import shopeazy.com.ecommerce_app.model.document.Permission;
import shopeazy.com.ecommerce_app.repository.PermissionRepository;

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
