package shopeazy.com.ecommerce_app.model.document;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document("roles")
public class Role {
    @Id
    private String id;
    private String name;
    private List<Permission> permissions;

    public Role(String name, List<Permission> permissions) {
        this.name = name;
        this.permissions = permissions;
    }
}
