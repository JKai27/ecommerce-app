package shopeazy.com.ecommerce_app.service.contracts;

import org.apache.coyote.BadRequestException;
import org.springframework.web.multipart.MultipartFile;
import shopeazy.com.ecommerce_app.model.document.Product;
import shopeazy.com.ecommerce_app.model.dto.request.CreateProductRequest;
import shopeazy.com.ecommerce_app.model.dto.response.ProductResponseDto;

import java.util.List;

public interface ProductService {
    List<Product> findAll();

    ProductResponseDto getProductById(String id);

    ProductResponseDto registerProduct(CreateProductRequest request);

    ProductResponseDto updateOwnProduct(String sellerId, UpdateProductRequestDto request);

    void deleteProductById(String productId);

    List<ProductResponseDto> updateOwnProductsInBulk(String sellerId, List<UpdateProductRequestDto> requestList);

    void deleteAllProductsBySellerId(String sellerId);

    List<String> uploadImages(List<MultipartFile> files, String productId) throws BadRequestException;
}
