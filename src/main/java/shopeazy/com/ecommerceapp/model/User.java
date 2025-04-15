package shopeazy.com.ecommerceapp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Document("users")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class User {

    @Id
    private String id;

    private String firstName;
    private String lastName;
    private String username;
    private String password;

    @Indexed(unique = true)
    private String email;

    private Gender gender;
    private String imageUrl;

    private Address address;
    @DBRef
    private List<Role> roles;

    private Status status;
    private Integer ordersCount;

    private Instant createdAt;
    private Instant updatedAt;

}
