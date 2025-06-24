package shopeazy.com.ecommerceapp.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import shopeazy.com.ecommerceapp.exceptions.ResourceNotFoundException;
import shopeazy.com.ecommerceapp.mapper.ProductMapper;
import shopeazy.com.ecommerceapp.model.document.Product;
import shopeazy.com.ecommerceapp.model.document.User;
import shopeazy.com.ecommerceapp.model.dto.request.CreateProductRequest;
import shopeazy.com.ecommerceapp.model.dto.response.ProductResponseDto;
import shopeazy.com.ecommerceapp.repository.UserRepository;
import shopeazy.com.ecommerceapp.service.contracts.UpdateProductRequestDto;
import shopeazy.com.ecommerceapp.service.serviceImplementation.ProductServiceImpl;

import java.nio.file.AccessDeniedException;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductServiceImpl productServiceImpl;
    private final UserRepository userRepository;

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
    @PreAuthorize("hasAuthority('ROLE_SELLER')")
    public ResponseEntity<ProductResponseDto> registerProduct(@RequestBody CreateProductRequest request) {
        ProductResponseDto productResponseDto = productServiceImpl.registerProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(productResponseDto);
    }

    @PutMapping("/update")
    @PreAuthorize("hasAuthority('ROLE_SELLER')")
    public ResponseEntity<ProductResponseDto> updateOwnProduct(
            @RequestBody UpdateProductRequestDto requestDto,
            Principal principal) throws AccessDeniedException {

        String sellerEmail = principal.getName();
        User seller = userRepository.findByEmail(sellerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Seller not found"));

        ProductResponseDto productResponseDto = productServiceImpl.updateOwnProduct(seller.getId(), requestDto);
        return ResponseEntity.ok(productResponseDto);
    }
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @DeleteMapping("{productId}")
    public ResponseEntity<String> deleteProductById(@PathVariable String productId) {
        productServiceImpl.deleteProductById(productId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}


