package shopeazy.com.ecommerce_app.shopping_cart.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import shopeazy.com.ecommerce_app.shopping_cart.model.pojo.CartItem;

import java.time.Instant;
import java.util.List;

@Data
public class CartResponse {
    @NotNull
    private String userId;
    private String userEmail;

    private List<CartItem> items;

    @CreatedDate
    private Instant createdAt;
    @LastModifiedDate
    private Instant updatedAt;
}
