package shopeazy.com.ecommerce_app.cart.dto;

import jakarta.validation.constraints.Min;
import lombok.Data;


@Data
public class AddProductsToCartRequest {
    private String productId;
    @Min(value = 1, message = "Quantity must be greater than zero")
    private int quantity;
}
