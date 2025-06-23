package shopeazy.com.ecommerceapp.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shopeazy.com.ecommerceapp.mapper.ProductMapper;
import shopeazy.com.ecommerceapp.model.document.Product;
import shopeazy.com.ecommerceapp.model.dto.request.CreateProductRequest;
import shopeazy.com.ecommerceapp.model.dto.response.ProductResponseDto;
import shopeazy.com.ecommerceapp.service.serviceImplementation.ProductServiceImpl;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductServiceImpl productServiceImpl;

    @GetMapping
    public ResponseEntity<List<ProductResponseDto>> getAllProducts() {
        List<Product> products = productServiceImpl.findAll();
        List<ProductResponseDto> responseDtoList = products.stream()
                .map(ProductMapper::mapToDto)
                .toList();
        return ResponseEntity.ok(responseDtoList);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponseDto> getProductById(@PathVariable String productId) {
        ProductResponseDto productById = productServiceImpl.getProductById(productId);
        return ResponseEntity.ok(productById);
    }

    @PostMapping
    public ResponseEntity<ProductResponseDto> registerProduct(@RequestBody CreateProductRequest request) {
        ProductResponseDto productResponseDto = productServiceImpl.registerProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(productResponseDto);
    }
}


