package shopeazy.com.ecommerce_app.product.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import shopeazy.com.ecommerce_app.common.exception.ResourceNotFoundException;
import shopeazy.com.ecommerce_app.product.dto.ProductAvailabilityResponse;
import shopeazy.com.ecommerce_app.user.model.User;
import shopeazy.com.ecommerce_app.product.dto.CreateProductRequest;
import shopeazy.com.ecommerce_app.product.dto.ProductResponseDto;
import shopeazy.com.ecommerce_app.product.repository.ProductRepository;
import shopeazy.com.ecommerce_app.user.repository.UserRepository;
import shopeazy.com.ecommerce_app.product.service.ProductService;
import shopeazy.com.ecommerce_app.product.dto.UpdateProductRequestDto;

import java.security.Principal;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @GetMapping
    public ResponseEntity<List<ProductResponseDto>> getAllProducts() {
        List<ProductResponseDto> responseDtoList = productService.getAllProducts();
        return ResponseEntity.ok(responseDtoList);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponseDto> getProductById(@PathVariable String productId) {
        ProductResponseDto productById = productService.getProductById(productId);
        return ResponseEntity.ok(productById);
    }

    @GetMapping("/{productId}/availability")
    public ResponseEntity<ProductAvailabilityResponse> checkProductAvailability(@PathVariable String productId) {
        ProductAvailabilityResponse productAvailabilityResponse = productService.checkProductAvailability(productId);
        return ResponseEntity.ok(productAvailabilityResponse);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_SELLER')")
    public ResponseEntity<ProductResponseDto> registerProduct(@Valid @RequestBody CreateProductRequest request) {
        ProductResponseDto productResponseDto = productService.registerProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(productResponseDto);
    }

    @PatchMapping("/update")
    @PreAuthorize("hasAuthority('ROLE_SELLER')")
    public ResponseEntity<ProductResponseDto> updateOwnProduct(
            @Valid @RequestBody UpdateProductRequestDto requestDto,
            Principal principal) {

        String sellerEmail = principal.getName();
        User seller = userRepository.findByEmail(sellerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Seller not found."));

        ProductResponseDto productResponseDto = productService.updateOwnProduct(seller.getId(), requestDto);
        return ResponseEntity.ok(productResponseDto);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @DeleteMapping("{productId}")
    public ResponseEntity<String> deleteProductById(@PathVariable String productId) {
        productService.deleteProductById(productId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /* Bulk Endpoints   */
    @PatchMapping("/bulk-update")
    @PreAuthorize("hasAuthority('ROLE_SELLER')")
    public ResponseEntity<List<ProductResponseDto>> updateOwnProductsInBulk(@Valid @RequestBody List<UpdateProductRequestDto> requestDtoList, Principal principal) {
        String sellerEmail = principal.getName();
        User seller = userRepository.findByEmail(sellerEmail).orElseThrow(() -> new ResourceNotFoundException("Seller not found"));
        List<ProductResponseDto> productResponseDtos = productService.updateOwnProductsInBulk(seller.getId(), requestDtoList);
        return ResponseEntity.ok(productResponseDtos);
    }


    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @DeleteMapping("/by-seller-id/{sellerId}")
    public ResponseEntity<Void> deleteAllProductsBySellerId(@PathVariable String sellerId) {
        productService.deleteAllProductsBySellerId(sellerId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}


