package shopeazy.com.ecommerceapp.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document("permissions")
public class Permission {
    @Id
    private String id;
    private String name;

    public Permission(String name) {
        this.name = name;
    }
}
