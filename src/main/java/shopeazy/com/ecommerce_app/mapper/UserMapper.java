package shopeazy.com.ecommerce_app.mapper;


import shopeazy.com.ecommerce_app.model.document.Permission;
import shopeazy.com.ecommerce_app.model.document.Role;
import shopeazy.com.ecommerce_app.model.document.User;
import shopeazy.com.ecommerce_app.model.dto.request.UserDTO;
import shopeazy.com.ecommerce_app.repository.RoleRepository;

import java.util.List;
import java.util.stream.Collectors;

public class UserMapper {

    public static UserDTO mapToDTO(User user, RoleRepository roleRepository) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUserNumber(user.getUserNumber());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setGender(user.getGender());
        dto.setImageUrl(user.getImageUrl());
        dto.setAddress(user.getAddress());
        dto.setStatus(user.getStatus());
        dto.setOrdersCount(user.getOrdersCount());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());

        List<Role> roles = roleRepository.findAllByNameIn(user.getRoles());
        dto.setRoles(user.getRoles());

        // Fetch permissions for all roles and map them
        List<List<String>> permissions = roles.stream()
                        .map(role -> role.getPermissions().stream()
                                .map(Permission::getName)
                                .toList())
                .collect(Collectors.toList());

        dto.setPermissions(permissions);
        return dto;
    }
}
