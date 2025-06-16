package shopeazy.com.ecommerceapp.model.document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "sequences")
public class DatabaseSequence {
    @Id
    private String id;
    private long sequence;
}
