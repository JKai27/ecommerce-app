package shopeazy.com.ecommerce_app.service.contracts;

import org.apache.coyote.BadRequestException;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductImageUploadService {
    List<String> uploadImages(List<MultipartFile> files, String productId) throws BadRequestException;
}
