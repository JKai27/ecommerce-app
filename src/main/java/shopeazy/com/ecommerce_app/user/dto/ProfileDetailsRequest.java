package shopeazy.com.ecommerce_app.user.dto;


import lombok.Data;

import java.util.List;

@Data
public class ProfileDetailsRequest {
    private String userId;
    private String firstName;
    private String lastName;
    private String email;
    private String gender;
    private List<String> roles;
}
