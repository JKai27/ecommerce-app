package shopeazy.com.ecommerceapp.service;

import org.springframework.stereotype.Service;
import shopeazy.com.ecommerceapp.model.Permission;
import shopeazy.com.ecommerceapp.repository.PermissionRepository;

import java.util.List;

@Service
public class PermissionService {
    private final PermissionRepository permissionRepository;

    public PermissionService(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }
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
