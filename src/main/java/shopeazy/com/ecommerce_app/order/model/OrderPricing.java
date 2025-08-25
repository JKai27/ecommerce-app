package shopeazy.com.ecommerce_app.order.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Represents the pricing breakdown for an order.
 * Embedded document containing all pricing information.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderPricing {
    
    /**
     * Subtotal before taxes and fees (sum of all item prices)
     */
    private BigDecimal subtotal;
    
    /**
     * Tax amount applied to order
     */
    private BigDecimal tax;
    
    /**
     * Shipping cost
     */
    private BigDecimal shipping;
    
    /**
     * Discount amount applied (positive value)
     */
    private BigDecimal discount;
    
    /**
     * Final total amount (subtotal + tax + shipping - discount)
     */
    private BigDecimal total;
    
    /**
     * Currency code (e.g., "USD", "INR")
     */
    private String currency;
}