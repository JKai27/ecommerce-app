package shopeazy.com.ecommerceapp.service;

import org.springframework.stereotype.Service;
import shopeazy.com.ecommerceapp.model.Role;
import shopeazy.com.ecommerceapp.repository.RoleRepository;

import java.util.List;

@Service
public class RoleService {
    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    // Get Role by name
    public Role getRoleByName(String roleName) {
        return roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));
    }

    // Get Roles by names
    public List<Role> getRolesByNames(List<String> roleNames) {
        return roleRepository.findAllByNameIn(roleNames);
    }
}
