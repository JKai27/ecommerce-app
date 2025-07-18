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

import java.security.Principal;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductImageController {
    private final ProductImagesManagementService productImagesManagementService;

    @PreAuthorize("hasAuthority('ROLE_SELLER')")
    @GetMapping("/{productId}/product-images")
    public ResponseEntity<List<String>> getProductImages(@PathVariable String productId, Principal principal) {
        return ResponseEntity.ok(productImagesManagementService.getImageUrlsForProduct(productId, principal.getName()));
    }

    @PostMapping("/{productId}/images")
    @PreAuthorize("hasAuthority('ROLE_SELLER')")
    public ResponseEntity<List<String>> uploadImages(
            @PathVariable String productId,
            @Valid @ModelAttribute ImageUploadRequestDTO imageUploadRequestDTO,
            Principal principal) throws BadRequestException {

        List<String> imageUrls = productImagesManagementService
                .uploadImages(imageUploadRequestDTO.getFiles(), productId, principal.getName());
        log.info("Uploading images for: {} successfully", productId);

        return ResponseEntity.ok(imageUrls);
    }

    @DeleteMapping("/{productId}/images/{sellersEmail}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_SELLER')")
    public ResponseEntity<String> deleteImage(@PathVariable String productId, @PathVariable String sellersEmail, @Valid @RequestBody DeleteImagesRequest request) throws BadRequestException {
        productImagesManagementService.deleteProductImages(productId, sellersEmail, request);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{productId}/images/order")
    @PreAuthorize("hasAuthority('ROLE_SELLER')")
    public ResponseEntity<ImageUrlsResponse> updateImageOrder(
            @PathVariable String productId,
            @Valid @RequestBody UpdateImagesOrderRequest orderRequest,
            Principal principal) {
        ImageOrderUpdateResult result = productImagesManagementService.updateImageOrder(productId, orderRequest, principal.getName());
        String message = result.updated() ? "Order of images updated successfully!"
                : "Order of images is already up to date. Please change the imageURL-order in the request.";
        return ResponseEntity.ok(new ImageUrlsResponse(result.images(), message));

    }
}
