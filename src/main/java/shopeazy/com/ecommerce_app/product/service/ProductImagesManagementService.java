package shopeazy.com.ecommerce_app.product.service;

import org.apache.coyote.BadRequestException;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductImagesManagementService {
    List<String> uploadImages(List<MultipartFile> files, String productId, String sellerEmail) throws BadRequestException;
    void validateProductOwner(String productId, String sellerEmail);
    List<String> getImageUrlsForProduct(String productId, String sellerEmail);
}
