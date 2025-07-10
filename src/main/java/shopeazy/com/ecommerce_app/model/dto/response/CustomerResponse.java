package shopeazy.com.ecommerce_app.model.dto.response;

import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
public class CustomerResponse {
    private String id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String gender;
    private String imageUrl;
    private AddressResponse address;
    private List<String> roles;
    private List<String> permissions;
    private String status;
    private Instant createdAt;
    private Instant updatedAt;
}
