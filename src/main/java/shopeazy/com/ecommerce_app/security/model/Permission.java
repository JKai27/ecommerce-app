package shopeazy.com.ecommerce_app.security.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@Document("permissions")
public class Permission {
    @Id
    private String id;
    @Indexed(unique = true)
    private String name;

    public Permission(String name) {
        this.name = name;
    }
}
