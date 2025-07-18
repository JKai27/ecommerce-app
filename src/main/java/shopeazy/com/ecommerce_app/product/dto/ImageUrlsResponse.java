package shopeazy.com.ecommerce_app.product.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ImageUrlsResponse {
    private List<String> imageUrls;
    private String message;
}