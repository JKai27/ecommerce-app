package shopeazy.com.ecommerce_app.product.service;

import shopeazy.com.ecommerce_app.product.model.Product;
import shopeazy.com.ecommerce_app.product.dto.CreateProductRequest;
import shopeazy.com.ecommerce_app.product.dto.ProductResponseDto;
import shopeazy.com.ecommerce_app.product.dto.UpdateProductRequestDto;

import java.util.List;

public interface ProductService {
    List<Product> findAll();

    ProductResponseDto getProductById(String id);

    ProductResponseDto registerProduct(CreateProductRequest request);

    ProductResponseDto updateOwnProduct(String sellerId, UpdateProductRequestDto request);

    void deleteProductById(String productId);

    List<ProductResponseDto> updateOwnProductsInBulk(String sellerId, List<UpdateProductRequestDto> requestList);

    void deleteAllProductsBySellerId(String sellerId);

}
