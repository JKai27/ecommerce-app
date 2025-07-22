package shopeazy.com.ecommerce_app.cart.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import shopeazy.com.ecommerce_app.cart.model.pojo.CartItem;

import java.time.Instant;
import java.util.List;

@Data
public class CartDTO {
    @NotNull
    private String userId;
    @NotBlank
    private List<CartItem> items;
    @CreatedDate
    private Instant createdAt;
    @LastModifiedDate
    private Instant updatedAt;
}
