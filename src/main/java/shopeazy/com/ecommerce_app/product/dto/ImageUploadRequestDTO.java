package shopeazy.com.ecommerce_app.product.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class ImageUploadRequestDTO {
    @NotNull
    private String productId;
    @NotNull(message = "sellerId is must")
    private String sellerId;
    @NotNull
    private List<MultipartFile> files;
}
