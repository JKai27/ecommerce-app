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

import java.security.Principal;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/carts")
public class CartController {
    private final CartService cartService;

    @PostMapping
    public ResponseEntity<CartResponse> addProductToCart(@RequestBody AddProductsToCartRequest request, Principal principal) {
        CartResponse cartResponse = cartService.addProductsToCart(request, principal.getName());
        cartResponse.setUserEmail(principal.getName());
        return ResponseEntity.ok(cartResponse);
    }

    @PutMapping("/update")
    public ResponseEntity<UpdatedCartInfoResponse> updateCartItem(
            @RequestBody @Valid UpdateCartRequest request,
            Principal principal) {

        UpdatedCartInfoResponse response = cartService.updateCartItems(request, principal.getName());
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<CartResponse> viewCart(Principal principal) {
        CartResponse cartResponse = cartService.viewCart(principal.getName());
        cartResponse.setUserEmail(principal.getName());
        return ResponseEntity.ok(cartResponse);
    }

    @DeleteMapping
    public ResponseEntity<Map<String, String>> clearCart(Principal principal) {
        cartService.clearCart(principal.getName());
        String message = "Cart for user " + principal.getName() + " has been cleared";
        return ResponseEntity.ok(Map.of("message", message));
    }

}
