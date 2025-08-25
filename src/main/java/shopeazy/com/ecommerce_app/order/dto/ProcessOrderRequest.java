package shopeazy.com.ecommerce_app.order.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for marking an order as processing.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProcessOrderRequest {
    
    /**
     * Optional notes about the processing (e.g., estimated completion time)
     */
    private String processingNotes;
    
    /**
     * Estimated processing time in hours
     */
    private Integer estimatedProcessingHours;
}