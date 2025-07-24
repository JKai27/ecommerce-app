package shopeazy.com.ecommerce_app.shopping_cart.model.pojo;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;


@Data
public class CartItem {

    @NotNull(message = "Product ID is required")
    private String productId;

    @Min(value = 1, message = "Quantity must be at least 1")
    private int productQuantity;

    @NotNull(message = "Product name is required")
    private String productName;

    private String productDescription;

    @NotNull(message = "Product price is required")
    private BigDecimal productPrice;
}