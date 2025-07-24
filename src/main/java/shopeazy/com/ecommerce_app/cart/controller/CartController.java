package shopeazy.com.ecommerce_app.cart.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shopeazy.com.ecommerce_app.cart.dto.AddProductsToCartRequest;
import shopeazy.com.ecommerce_app.cart.dto.CartResponse;
import shopeazy.com.ecommerce_app.cart.dto.UpdateCartRequest;
import shopeazy.com.ecommerce_app.cart.dto.UpdatedCartInfoResponse;
import shopeazy.com.ecommerce_app.cart.service.CartService;

import java.security.Principal;

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
    public ResponseEntity<Void> clearCart(Principal principal) {
        cartService.clearCart(principal.getName());
        return ResponseEntity.noContent().build();
    }
}
