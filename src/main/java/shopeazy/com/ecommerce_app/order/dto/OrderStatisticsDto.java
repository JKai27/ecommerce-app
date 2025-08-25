package shopeazy.com.ecommerce_app.order.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for order statistics and metrics.
 * Used for admin dashboard and reporting.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderStatisticsDto {
    
    private long totalOrders;
    private long pendingOrders;
    private long confirmedOrders;
    private long processingOrders;
    private long shippedOrders;
    private long deliveredOrders;
    private long cancelledOrders;
    private long completedOrders;
    
    private BigDecimal totalRevenue;
    private BigDecimal pendingRevenue;
    private BigDecimal completedRevenue;
    
    private double averageOrderValue;
    private double cancellationRate;
    private double fulfillmentRate;
}