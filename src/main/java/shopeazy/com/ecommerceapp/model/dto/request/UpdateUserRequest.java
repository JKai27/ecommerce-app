package shopeazy.com.ecommerceapp.model.dto.request;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.DBRef;
import shopeazy.com.ecommerceapp.model.document.Address;
import shopeazy.com.ecommerceapp.enums.Gender;
import shopeazy.com.ecommerceapp.model.document.Role;
import shopeazy.com.ecommerceapp.service.serviceImplementation.ValidPassword;

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
