package shopeazy.com.ecommerceapp.service.contracts;

import shopeazy.com.ecommerceapp.model.document.Product;
import shopeazy.com.ecommerceapp.model.dto.request.CreateProductRequest;
import shopeazy.com.ecommerceapp.model.dto.response.ProductResponseDto;

import java.nio.file.AccessDeniedException;
import java.util.List;

public interface ProductService {
    List<Product> findAll();

    ProductResponseDto getProductById(String id);

    ProductResponseDto registerProduct(CreateProductRequest request);
    ProductResponseDto updateOwnProduct(String sellerId, UpdateProductRequestDto request);
}
