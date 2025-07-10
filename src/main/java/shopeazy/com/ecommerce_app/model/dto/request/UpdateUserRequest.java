package shopeazy.com.ecommerce_app.model.dto.request;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.DBRef;
import shopeazy.com.ecommerce_app.model.document.Address;
import shopeazy.com.ecommerce_app.enums.Gender;
import shopeazy.com.ecommerce_app.model.document.Role;
import shopeazy.com.ecommerce_app.service.implementation.ValidPassword;

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
