package shopeazy.com.ecommerce_app.orders.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {

    private String productId;

    private String productName;

    private String productDescription;

    /**
     * Quantity ordered
     */
    private Integer quantity;

    /**
     * Unit price at the time of purchase (snapshot for history)
     */
    private BigDecimal priceAtTime;

    /**
     * Total price for this line item (quantity * priceAtTime)
     */
    private BigDecimal totalPrice;

    /**
     * Seller ID who owns this product
     */
    private String sellerId;

    /**
     * Seller company name at time of purchase
     */
    private String sellerName;

}
