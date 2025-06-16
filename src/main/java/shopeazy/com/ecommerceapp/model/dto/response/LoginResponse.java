package shopeazy.com.ecommerceapp.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import shopeazy.com.ecommerceapp.enums.Gender;

import java.util.List;

@Data
@AllArgsConstructor
public class LoginResponse {
    private String id;
    private String firstName;
    private String lastName;
    private Gender gender;
    private String email;
    private boolean success;
    private List<String> roles;
}