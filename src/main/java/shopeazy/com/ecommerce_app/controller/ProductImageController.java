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
import shopeazy.com.ecommerce_app.service.contracts.ProductImagesManagementService;

import java.nio.file.AccessDeniedException;
import java.security.Principal;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductImageController {
    private final ProductImagesManagementService productImagesManagementService;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;


    @PostMapping("/{productId}/images")
    @PreAuthorize("hasAuthority('ROLE_SELLER')")
    public ResponseEntity<List<String>> uploadImages(
            @PathVariable String productId,
            @Valid @ModelAttribute ImageUploadRequestDTO imageUploadRequestDTO,
            Principal principal) throws BadRequestException {

        List<String> imageUrls = productImagesManagementService
                .uploadImages(imageUploadRequestDTO.getFiles(), productId, principal.getName());

        return ResponseEntity.ok(imageUrls);
    }
}
