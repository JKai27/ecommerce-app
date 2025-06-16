package shopeazy.com.ecommerceapp.model.document;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
@Data
@Document
public class Token {
    @Id
    private String id;

    private String value;
    private boolean disabled;
    private LocalDateTime expiryDate;

    @DBRef
    private User user;

}
