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

    // New pricing fields
    @NotNull(message = "Original price is required")
    private BigDecimal originalPrice;
    
    @NotNull(message = "Discounted price is required")
    private BigDecimal discountedPrice;
    
    @NotNull(message = "Discount percentage is required")
    private BigDecimal discount;         // Percentage

    // Computed field for backward compatibility
    public BigDecimal getProductPrice() {
        return discountedPrice != null ? discountedPrice : BigDecimal.ZERO;
    }

    // Constructor for backward compatibility and default values
    public CartItem() {
        this.originalPrice = BigDecimal.ZERO;
        this.discountedPrice = BigDecimal.ZERO;
        this.discount = BigDecimal.ZERO;
    }
}