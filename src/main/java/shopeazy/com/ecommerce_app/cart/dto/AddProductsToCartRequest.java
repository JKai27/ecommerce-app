package shopeazy.com.ecommerce_app.cart.dto;

import lombok.Data;


@Data
public class AddProductsToCartRequest {
    private String productId;
    private String userId;
    private int quantity;
}
