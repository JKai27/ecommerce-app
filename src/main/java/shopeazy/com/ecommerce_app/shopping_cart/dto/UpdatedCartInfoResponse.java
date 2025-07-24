package shopeazy.com.ecommerce_app.shopping_cart.dto;

import lombok.Data;
import shopeazy.com.ecommerce_app.shopping_cart.model.pojo.RemovedProductItem;

import java.util.List;

@Data
public class UpdatedCartInfoResponse {
    private List<RemovedProductItem> removedProducts;
    private CartResponse cart;
}
