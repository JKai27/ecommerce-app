package shopeazy.com.ecommerce_app.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import shopeazy.com.ecommerce_app.enums.Gender;

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