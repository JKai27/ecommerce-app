package shopeazy.com.ecommerceapp.model.document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("user-refresh-tokens")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRefreshToken {
    @Id
    private String id;
    @Indexed(unique = true)
    private String token;
   private String userId; // instead of @DBRef - performant and we don't require here a complete User Object

    public UserRefreshToken(String token, String userId) {
        this.token = token;
        this.userId = userId;
    }
}
