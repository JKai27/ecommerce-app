package shopeazy.com.ecommerce_app.product.dto;

import lombok.Data;
import shopeazy.com.ecommerce_app.seller.dto.SellerDto;

import java.time.Instant;
import java.util.List;

@Data
public class ProductResponseDto {
    private String id;
    private String productNumber;
    private String name;
    private String description;
    private Double price;
    private Double discount;
    private Integer stockCount;
    private String category;
    private List<String> images;
    private String status;
    private SellerDto seller;
    private Instant createdAt;
    private Instant updatedAt;
}