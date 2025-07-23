package shopeazy.com.ecommerce_app.cart.service;

import org.springframework.transaction.annotation.Transactional;
import shopeazy.com.ecommerce_app.cart.dto.AddProductsToCartRequest;
import shopeazy.com.ecommerce_app.cart.dto.CartResponse;
import shopeazy.com.ecommerce_app.cart.dto.UpdateCartRequest;
import shopeazy.com.ecommerce_app.cart.dto.UpdatedCartInfoResponse;

public interface CartService {
    CartResponse addProductsToCart(AddProductsToCartRequest request);

    UpdatedCartInfoResponse updateCartItems(UpdateCartRequest request, String usersEmail);

    CartResponse viewCart(String userEmail);

    @Transactional
    void clearCart(String userEmail);
}
