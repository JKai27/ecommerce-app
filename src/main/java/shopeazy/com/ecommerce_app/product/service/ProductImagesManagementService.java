package shopeazy.com.ecommerce_app.product.service;

import org.apache.coyote.BadRequestException;
import org.springframework.web.multipart.MultipartFile;
import shopeazy.com.ecommerce_app.product.dto.DeleteImagesRequest;
import shopeazy.com.ecommerce_app.product.dto.UpdateImagesOrderRequest;

import java.util.List;

public interface ProductImagesManagementService {
    List<String> uploadImages(List<MultipartFile> files, String productId, String sellerEmail) throws BadRequestException;
    void validateProductOwner(String productId, String sellerEmail);

    void deleteProductImages(String productId, String sellerEmail, DeleteImagesRequest request) throws BadRequestException;

    List<String> getImageUrlsForProduct(String productId, String sellerEmail);

    List<String> updateImageOrder(String productId, UpdateImagesOrderRequest orderRequest, String sellerEmail);
}
