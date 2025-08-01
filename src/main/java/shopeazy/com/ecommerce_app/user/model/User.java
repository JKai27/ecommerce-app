package shopeazy.com.ecommerce_app.user.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import shopeazy.com.ecommerce_app.common.enums.Gender;
import shopeazy.com.ecommerce_app.common.enums.Status;
import shopeazy.com.ecommerce_app.common.model.Address;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Document("users")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class User {

    @Id
    private String id;
    @Indexed(unique = true)
    private String userNumber;

    private String firstName;
    private String lastName;
    private String username;
    private String password;

    @Indexed(unique = true)
    private String email;

    private Gender gender;
    private String imageUrl;

    private Address address;

    private List<String> roles = new ArrayList<>();

    private Status status;
    private Integer ordersCount;

    private Instant createdAt;
    private Instant updatedAt;
}
