package shopeazy.com.ecommerce_app.cart.service;

import shopeazy.com.ecommerce_app.cart.dto.AddProductsToCartRequest;
import shopeazy.com.ecommerce_app.product.dto.ProductResponseDto;

public interface CartService {
    ProductResponseDto addProductsToCart(AddProductsToCartRequest request);
}
