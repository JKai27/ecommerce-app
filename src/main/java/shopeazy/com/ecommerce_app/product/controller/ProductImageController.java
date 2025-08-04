package shopeazy.com.ecommerce_app.product.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import shopeazy.com.ecommerce_app.product.dto.*;
import shopeazy.com.ecommerce_app.product.service.ProductImagesManagementService;
import shopeazy.com.ecommerce_app.common.dto.ApiResponse;

import java.security.Principal;
import java.time.Instant;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductImageController {
    private final ProductImagesManagementService productImagesManagementService;

    @PreAuthorize("hasAuthority('ROLE_SELLER')")
    @GetMapping("/{productId}/product-images")
    public ResponseEntity<ApiResponse<List<String>>> getProductImages(@PathVariable String productId, Principal principal) {
        List<String> imageUrls = productImagesManagementService.getImageUrlsForProduct(productId, principal.getName());
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Product images retrieved successfully", imageUrls, Instant.now())
        );
    }

    @PostMapping("/{productId}/images")
    @PreAuthorize("hasAuthority('ROLE_SELLER')")
    public ResponseEntity<ApiResponse<List<String>>> uploadImages(
            @PathVariable String productId,
            @Valid @ModelAttribute ImageUploadRequestDTO imageUploadRequestDTO,
            Principal principal) throws BadRequestException {

        List<String> imageUrls = productImagesManagementService
                .uploadImages(imageUploadRequestDTO.getFiles(), productId, principal.getName());
        log.info("Uploading images for: {} successfully", productId);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Images uploaded successfully", imageUrls, Instant.now())
        );
    }

    @DeleteMapping("/{productId}/images/{sellersEmail}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_SELLER')")
    public ResponseEntity<ApiResponse<String>> deleteImage(@PathVariable String productId, @PathVariable String sellersEmail, @Valid @RequestBody DeleteImagesRequest request) throws BadRequestException {
        productImagesManagementService.deleteProductImages(productId, sellersEmail, request);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Images deleted successfully", null, Instant.now())
        );
    }

    @PatchMapping("/{productId}/images/order")
    @PreAuthorize("hasAuthority('ROLE_SELLER')")
    public ResponseEntity<ApiResponse<ImageUrlsResponse>> updateImageOrder(
            @PathVariable String productId,
            @Valid @RequestBody UpdateImagesOrderRequest orderRequest,
            Principal principal) {
        ImageOrderUpdateResult result = productImagesManagementService.updateImageOrder(productId, orderRequest, principal.getName());
        String message = result.updated() ? "Order of images updated successfully!"
                : "Order of images is already up to date. Please change the imageURL-order in the request.";
        ImageUrlsResponse imageUrlsResponse = new ImageUrlsResponse(result.images(), message);
        return ResponseEntity.ok(
                new ApiResponse<>(true, message, imageUrlsResponse, Instant.now())
        );
    }
}
