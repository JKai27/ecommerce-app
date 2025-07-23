package shopeazy.com.ecommerce_app.cart.model.pojo;

import lombok.Data;

@Data
public class RemovedProductItem {
    private String productId;
    private int quantity;
}
