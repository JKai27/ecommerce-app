package shopeazy.com.ecommerce_app.product.service;

import jakarta.validation.Valid;
import shopeazy.com.ecommerce_app.product.dto.*;
import shopeazy.com.ecommerce_app.product.model.Product;

import java.util.List;

public interface ProductService {
    List<Product> findAll();
    
    List<ProductResponseDto> getAllProducts();

    ProductResponseDto getProductById(String id);

    ProductResponseDto registerProduct(CreateProductRequest request, String sellerId);

    ProductResponseDto updateOwnProduct(String sellerId, UpdateProductRequestDto request);

    ProductResponseDto updateProductStatus(UpdateProductStatusRequest request);

    List<ProductResponseDto> bulkUpdateMultipleProductStatus(BulkUpdateMultipleProductStatusRequest request);

    void deleteProductById(String productId);

    List<ProductResponseDto> updateOwnProductsInBulk(String sellerId, List<UpdateProductRequestDto> requestList);

    void deleteAllProductsBySellerId(String sellerId);

    ProductAvailabilityResponse checkProductAvailability(String productId);

    void validateRequestedQuantity(String productId, int requestedQty);
    void restoreStock(String productId, int quantityToRestore);

    List<ProductResponseDto> bulkUpdateProductStatus(@Valid BulkUpdateProductStatusRequest request);
}
