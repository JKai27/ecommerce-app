package shopeazy.com.ecommerce_app.user.dto;

import lombok.Data;
import shopeazy.com.ecommerce_app.common.model.Address;
import shopeazy.com.ecommerce_app.common.enums.Gender;
import shopeazy.com.ecommerce_app.user.validator.ValidPassword;

@Data
public class UpdateUserProfileRequest {
    private String firstName;
    private String lastName;
    private String username;
    @ValidPassword
    private String password;

    private Gender gender;
    private String imageUrl;

    private Address address;
}
