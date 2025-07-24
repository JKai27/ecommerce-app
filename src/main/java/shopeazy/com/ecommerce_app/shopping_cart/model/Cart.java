package shopeazy.com.ecommerce_app.shopping_cart.model;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import shopeazy.com.ecommerce_app.shopping_cart.model.pojo.CartItem;

import java.time.Instant;
import java.util.List;

@Data
@Document
public class Cart {
    @Id
    private String cartId;
    @NotNull
    private String userId;
    private List<CartItem> items;
    @CreatedDate
    private Instant createdAt;
    @LastModifiedDate
    private Instant updatedAt;
}
