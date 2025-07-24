package shopeazy.com.ecommerce_app.shopping_cart.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import shopeazy.com.ecommerce_app.shopping_cart.enums.CartAction;

@Data
public class UpdateCartRequest {
    private String productId;
    @PositiveOrZero(message = "Quantity must be zero or greater")
    private int quantity;
    @NotNull(message = "Action to perform update is necessary. Choose add or remove action")
    private CartAction action;
}

