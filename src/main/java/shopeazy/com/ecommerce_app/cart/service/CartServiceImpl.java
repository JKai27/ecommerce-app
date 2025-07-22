package shopeazy.com.ecommerce_app.cart.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import shopeazy.com.ecommerce_app.cart.dto.AddProductsToCartRequest;
import shopeazy.com.ecommerce_app.cart.model.Cart;
import shopeazy.com.ecommerce_app.cart.model.pojo.CartItem;
import shopeazy.com.ecommerce_app.cart.repository.CartRepository;
import shopeazy.com.ecommerce_app.common.exception.ResourceNotFoundException;
import shopeazy.com.ecommerce_app.product.dto.ProductAvailabilityResponse;
import shopeazy.com.ecommerce_app.product.dto.ProductResponseDto;
import shopeazy.com.ecommerce_app.product.mapper.ProductMapper;
import shopeazy.com.ecommerce_app.product.model.Product;
import shopeazy.com.ecommerce_app.product.repository.ProductRepository;
import shopeazy.com.ecommerce_app.product.service.ProductService;
import shopeazy.com.ecommerce_app.user.model.User;
import shopeazy.com.ecommerce_app.user.repository.UserRepository;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final ProductService productService;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Override
    public ProductResponseDto addProductsToCart(AddProductsToCartRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(ResourceNotFoundException::new);

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(ResourceNotFoundException::new);

        ProductAvailabilityResponse productAvailabilityResponse = productService.checkProductAvailability(request.getProductId());

        // updating product stock
        product.setStockCount(product.getStockCount() - request.getQuantity());
        productRepository.save(product);

        if (request.getQuantity() > productAvailabilityResponse.getProductStockCount()) {
            throw new IllegalArgumentException("Requested quantity exceeds available stock");
        }


        Cart cart = cartRepository.findByUserId(user.getId()).orElseGet(() -> {
            Cart newCart = new Cart();
            newCart.setUserId(user.getId());
            newCart.setItems(new ArrayList<>());
            newCart.setCreatedAt(Instant.now());
            return newCart;
        });


        Optional<CartItem> existingItemOption = cart.getItems().stream()
                .filter(item -> item.getProductId().equals(product.getId()))
                .findFirst();

        if (existingItemOption.isPresent()) {
            CartItem existingItem = existingItemOption.get();
            existingItem.setProductQuantity(existingItem.getProductQuantity() + request.getQuantity());
        } else {
            CartItem newItem = new CartItem();
            newItem.setProductId(product.getId());
            newItem.setProductName(product.getName());
            newItem.setProductDescription(product.getDescription());
            newItem.setProductPrice(BigDecimal.valueOf(product.getPrice()));
            newItem.setProductQuantity(request.getQuantity());

            cart.getItems().add(newItem);
        }

        cart.setUpdatedAt(Instant.now());
        cartRepository.save(cart);

        return ProductMapper.mapToDto(product);

    }
}
