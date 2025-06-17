package shopeazy.com.ecommerceapp.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import shopeazy.com.ecommerceapp.mapper.ProductMapper;
import shopeazy.com.ecommerceapp.model.document.Product;
import shopeazy.com.ecommerceapp.model.dto.response.ProductResponseDto;
import shopeazy.com.ecommerceapp.service.serviceImplementation.ProductService;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @GetMapping
    public ResponseEntity<List<ProductResponseDto>> getAllProducts() {
        List<Product> products = productService.findAll();
        List<ProductResponseDto> responseDtoList = products.stream()
                .map(ProductMapper::mapToDto)
                .toList();
        return ResponseEntity.ok(responseDtoList);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponseDto> getProductById(@PathVariable String productId) {
        ProductResponseDto productById = productService.getProductById(productId);
        return ResponseEntity.ok(productById);
    }
}


