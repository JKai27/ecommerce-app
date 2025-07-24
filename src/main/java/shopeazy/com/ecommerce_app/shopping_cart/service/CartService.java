package shopeazy.com.ecommerce_app.shopping_cart.service;

import org.springframework.transaction.annotation.Transactional;
import shopeazy.com.ecommerce_app.shopping_cart.dto.AddProductsToCartRequest;
import shopeazy.com.ecommerce_app.shopping_cart.dto.CartResponse;
import shopeazy.com.ecommerce_app.shopping_cart.dto.UpdateCartRequest;
import shopeazy.com.ecommerce_app.shopping_cart.dto.UpdatedCartInfoResponse;

public interface CartService {
    CartResponse addProductsToCart(AddProductsToCartRequest request, String userEmail);

    UpdatedCartInfoResponse updateCartItems(UpdateCartRequest request, String usersEmail);

    CartResponse viewCart(String userEmail);

    @Transactional
    void clearCart(String userEmail);
}
