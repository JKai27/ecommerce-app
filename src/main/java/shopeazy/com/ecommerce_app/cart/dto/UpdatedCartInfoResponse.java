package shopeazy.com.ecommerce_app.cart.dto;

import lombok.Data;
import shopeazy.com.ecommerce_app.cart.model.pojo.RemovedProductItem;

import java.util.List;

@Data
public class UpdatedCartInfoResponse {
    private List<RemovedProductItem> removedProducts;
    private CartResponse cart;
}
