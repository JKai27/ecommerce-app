package shopeazy.com.ecommerceapp.model.dto.request;

import lombok.Data;
import shopeazy.com.ecommerceapp.model.document.Address;
import shopeazy.com.ecommerceapp.enums.Gender;
import shopeazy.com.ecommerceapp.enums.Status;

import java.time.Instant;
import java.util.List;
@Data
public class UserDTO {
    private String id;
    private String userNumber;
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private Gender gender;
    private String imageUrl;
    private Address address;
    private List<String> roles;
    private List<List<String>> permissions;
    private Status status;
    private Integer ordersCount;
    private Instant createdAt;
    private Instant updatedAt;
}
