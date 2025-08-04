package shopeazy.com.ecommerce_app.shopping_cart.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shopeazy.com.ecommerce_app.shopping_cart.dto.AddProductsToCartRequest;
import shopeazy.com.ecommerce_app.shopping_cart.dto.CartResponse;
import shopeazy.com.ecommerce_app.shopping_cart.dto.UpdateCartRequest;
import shopeazy.com.ecommerce_app.shopping_cart.dto.UpdatedCartInfoResponse;
import shopeazy.com.ecommerce_app.shopping_cart.service.CartService;
import shopeazy.com.ecommerce_app.common.dto.ApiResponse;

import java.security.Principal;
import java.time.Instant;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/carts")
public class CartController {
    private final CartService cartService;

    @PostMapping
    public ResponseEntity<ApiResponse<CartResponse>> addProductToCart(@RequestBody AddProductsToCartRequest request, Principal principal) {
        CartResponse cartResponse = cartService.addProductsToCart(request, principal.getName());
        cartResponse.setUserEmail(principal.getName());
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Products added to cart successfully", cartResponse, Instant.now())
        );
    }

    @PutMapping("/update")
    public ResponseEntity<ApiResponse<UpdatedCartInfoResponse>> updateCartItem(
            @RequestBody @Valid UpdateCartRequest request,
            Principal principal) {

        UpdatedCartInfoResponse response = cartService.updateCartItems(request, principal.getName());
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Cart items updated successfully", response, Instant.now())
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<CartResponse>> viewCart(Principal principal) {
        CartResponse cartResponse = cartService.viewCart(principal.getName());
        cartResponse.setUserEmail(principal.getName());
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Cart retrieved successfully", cartResponse, Instant.now())
        );
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<String>> clearCart(Principal principal) {
        cartService.clearCart(principal.getName());
        String message = "Cart for user " + principal.getName() + " has been cleared";
        return ResponseEntity.ok(
                new ApiResponse<>(true, message, null, Instant.now())
        );
    }

}
