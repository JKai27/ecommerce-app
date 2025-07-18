package shopeazy.com.ecommerce_app.product.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import shopeazy.com.ecommerce_app.product.dto.ImageUploadRequestDTO;
import shopeazy.com.ecommerce_app.product.service.ProductImagesManagementService;

import java.security.Principal;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductImageController {
    private final ProductImagesManagementService productImagesManagementService;

    @GetMapping("/{productId}/product-images")
    public ResponseEntity<List<String>> getProductImages(@PathVariable String productId, Principal principal ) {
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

    // TODO
    @DeleteMapping("/{productId}/images/{imageId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_SELLER')")
    public ResponseEntity<String> deleteImage(@PathVariable String productId, @PathVariable String imageId, Principal principal){
        productImagesManagementService.validateProductOwner(productId, principal.getName());
        return ResponseEntity.noContent().build();
    }
}
