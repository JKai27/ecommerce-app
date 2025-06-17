package shopeazy.com.ecommerceapp.model.dto.response;

import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
public class ProductResponseDto {
    private String id;
    private String name;
    private String description;
    private Double price;
    private Double discount;
    private Integer stockCount;
    private String category;
    private List<String> images;
    private String status;
    private String sellerId;
    private Instant createdAt;
    private Instant updatedAt;
}