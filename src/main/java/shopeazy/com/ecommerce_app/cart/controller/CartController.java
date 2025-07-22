package shopeazy.com.ecommerce_app.cart.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import shopeazy.com.ecommerce_app.cart.dto.AddProductsToCartRequest;
import shopeazy.com.ecommerce_app.cart.service.CartService;
import shopeazy.com.ecommerce_app.product.dto.ProductResponseDto;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/carts")
public class CartController {
    private final CartService cartService;

    @PostMapping
    public ResponseEntity<ProductResponseDto> addProductToCart(@RequestBody AddProductsToCartRequest request) {
        ProductResponseDto productResponseDto = cartService.addProductsToCart(request);
        return ResponseEntity.ok(productResponseDto);
    }
}
