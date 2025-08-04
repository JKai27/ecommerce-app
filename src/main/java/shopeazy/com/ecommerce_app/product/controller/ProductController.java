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
import shopeazy.com.ecommerce_app.product.dto.*;
import shopeazy.com.ecommerce_app.user.model.User;
import shopeazy.com.ecommerce_app.user.repository.UserRepository;
import shopeazy.com.ecommerce_app.product.service.ProductService;
import shopeazy.com.ecommerce_app.common.dto.ApiResponse;

import java.security.Principal;
import java.time.Instant;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductResponseDto>>> getAllProducts() {
        List<ProductResponseDto> responseDtoList = productService.getAllProducts();
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Products retrieved successfully", responseDtoList, Instant.now())
        );
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ApiResponse<ProductResponseDto>> getProductById(@PathVariable String productId) {
        ProductResponseDto productById = productService.getProductById(productId);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Product retrieved successfully", productById, Instant.now())
        );
    }

    @GetMapping("/{productId}/availability")
    public ResponseEntity<ApiResponse<ProductAvailabilityResponse>> checkProductAvailability(@PathVariable String productId) {
        ProductAvailabilityResponse productAvailabilityResponse = productService.checkProductAvailability(productId);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Product availability checked successfully", productAvailabilityResponse, Instant.now())
        );
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_SELLER')")
    public ResponseEntity<ApiResponse<ProductResponseDto>> registerProduct(@Valid @RequestBody CreateProductRequest request) {
        ProductResponseDto productResponseDto = productService.registerProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ApiResponse<>(true, "Product registered successfully", productResponseDto, Instant.now())
        );
    }

    @PatchMapping("/update")
    @PreAuthorize("hasAuthority('ROLE_SELLER')")
    public ResponseEntity<ApiResponse<ProductResponseDto>> updateOwnProduct(
            @Valid @RequestBody UpdateProductRequestDto requestDto,
            Principal principal) {

        String sellerEmail = principal.getName();
        User seller = userRepository.findByEmail(sellerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Seller not found."));

        ProductResponseDto productResponseDto = productService.updateOwnProduct(seller.getId(), requestDto);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Product updated successfully", productResponseDto, Instant.now())
        );
    }

    @PatchMapping("/status")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<ProductResponseDto>> updateProductStatus(
            @Valid @RequestBody UpdateProductStatusRequest request) {
        ProductResponseDto responseDto = productService.updateProductStatus(request);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Product status updated successfully", responseDto, Instant.now())
        );
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PatchMapping("/status/bulk")
    public ResponseEntity<ApiResponse<List<ProductResponseDto>>> bulkUpdateProductStatus(
            @Valid @RequestBody BulkUpdateProductStatusRequest request) {
        List<ProductResponseDto> updatedProducts = productService.bulkUpdateProductStatus(request);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Product statuses updated successfully", updatedProducts, Instant.now())
        );
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PatchMapping("/multi-status/bulk")
    public ResponseEntity<ApiResponse<List<ProductResponseDto>>> bulkUpdateMultipleProductStatus(
            @Valid @RequestBody BulkUpdateMultipleProductStatusRequest request) {
        List<ProductResponseDto> updatedProducts = productService.bulkUpdateMultipleProductStatus(request);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Multiple product statuses updated successfully", updatedProducts, Instant.now())
        );
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_SELLER')")
    @DeleteMapping("{productId}")
    public ResponseEntity<String> deleteProductById(@PathVariable String productId) {
        productService.deleteProductById(productId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /* Bulk Endpoints   */
    @PatchMapping("/bulk-update")
    @PreAuthorize("hasAuthority('ROLE_SELLER')")
    public ResponseEntity<ApiResponse<List<ProductResponseDto>>> updateOwnProductsInBulk(
            @Valid @RequestBody List<UpdateProductRequestDto> requestDtoList, Principal principal) {
        String sellerEmail = principal.getName();
        User seller = userRepository.findByEmail(sellerEmail).orElseThrow(() -> new ResourceNotFoundException("Seller not found"));
        List<ProductResponseDto> productResponseDtos = productService.updateOwnProductsInBulk(seller.getId(), requestDtoList);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Products updated successfully in bulk", productResponseDtos, Instant.now())
        );
    }


    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @DeleteMapping("/by-seller-id/{sellerId}")
    public ResponseEntity<Void> deleteAllProductsBySellerId(@PathVariable String sellerId) {
        productService.deleteAllProductsBySellerId(sellerId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}


