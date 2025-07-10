package shopeazy.com.ecommerce_app.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import shopeazy.com.ecommerce_app.exceptions.ResourceNotFoundException;
import shopeazy.com.ecommerce_app.mapper.ProductMapper;
import shopeazy.com.ecommerce_app.model.document.Product;
import shopeazy.com.ecommerce_app.model.document.User;
import shopeazy.com.ecommerce_app.model.dto.request.CreateProductRequest;
import shopeazy.com.ecommerce_app.model.dto.request.ImageUploadRequestDTO;
import shopeazy.com.ecommerce_app.model.dto.response.ProductResponseDto;
import shopeazy.com.ecommerce_app.repository.ProductRepository;
import shopeazy.com.ecommerce_app.repository.UserRepository;
import shopeazy.com.ecommerce_app.service.contracts.ProductService;
import shopeazy.com.ecommerce_app.service.contracts.UpdateProductRequestDto;

import java.nio.file.AccessDeniedException;
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

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_SELLER')")
    public ResponseEntity<ProductResponseDto> registerProduct(@Valid @RequestBody CreateProductRequest request) {
        ProductResponseDto productResponseDto = productService.registerProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(productResponseDto);
    }

    @PutMapping("/update")
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
    @PutMapping("/bulk-update")
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

        List<String> imageUrls = productService.uploadImages(imageUploadRequestDTO.getFiles(), productId);
        product.setImages(imageUrls);
        productRepository.save(product);

        return ResponseEntity.ok(imageUrls);
    }

}


