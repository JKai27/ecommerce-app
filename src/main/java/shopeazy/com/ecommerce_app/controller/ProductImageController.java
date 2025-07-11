package shopeazy.com.ecommerce_app.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import shopeazy.com.ecommerce_app.exceptions.ResourceNotFoundException;
import shopeazy.com.ecommerce_app.model.document.Product;
import shopeazy.com.ecommerce_app.model.document.User;
import shopeazy.com.ecommerce_app.model.dto.request.ImageUploadRequestDTO;
import shopeazy.com.ecommerce_app.repository.ProductRepository;
import shopeazy.com.ecommerce_app.repository.UserRepository;
import shopeazy.com.ecommerce_app.service.contracts.ProductImageUploadService;

import java.nio.file.AccessDeniedException;
import java.security.Principal;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductImageController {
    private final ProductImageUploadService productImageUploadService;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;


    @PostMapping("/{productId}/images")
    @PreAuthorize("hasAuthority('ROLE_SELLER')")
    public ResponseEntity<List<String>> uploadImages(
            @PathVariable String productId,
            @Valid @ModelAttribute ImageUploadRequestDTO imageUploadRequestDTO,
            Principal principal) throws AccessDeniedException, BadRequestException {

        // Get the seller's email from the principal (authenticated user)
        String sellerEmail = principal.getName();

        // Fetch the seller (user) based on email
        User seller = userRepository.findByEmail(sellerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Seller not found"));

        // Validate that the seller owns the product
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        if (!product.getSellerId().equals(seller.getId())) {
            throw new AccessDeniedException("Seller does not own this product.");
        }

        List<String> imageUrls = productImageUploadService.uploadImages(imageUploadRequestDTO.getFiles(), productId);
        product.setImages(imageUrls);
        productRepository.save(product);

        return ResponseEntity.ok(imageUrls);
    }


}
