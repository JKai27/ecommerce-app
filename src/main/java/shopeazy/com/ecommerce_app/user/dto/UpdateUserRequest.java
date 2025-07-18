package shopeazy.com.ecommerce_app.user.dto;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.DBRef;
import shopeazy.com.ecommerce_app.common.model.Address;
import shopeazy.com.ecommerce_app.common.enums.Gender;
import shopeazy.com.ecommerce_app.security.model.Role;
import shopeazy.com.ecommerce_app.user.validator.ValidPassword;

import java.util.List;
@Data
public class UpdateUserRequest {
    private String firstName;
    private String lastName;
    private String username;
    @ValidPassword
    private String password;

    private String email;

    private Gender gender;
    private String imageUrl;

    private Address address;
    @DBRef
    private List<Role> roles;
}
