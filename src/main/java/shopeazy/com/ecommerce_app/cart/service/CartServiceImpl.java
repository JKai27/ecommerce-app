package shopeazy.com.ecommerce_app.cart.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shopeazy.com.ecommerce_app.cart.dto.AddProductsToCartRequest;
import shopeazy.com.ecommerce_app.cart.dto.CartResponse;
import shopeazy.com.ecommerce_app.cart.dto.UpdateCartRequest;
import shopeazy.com.ecommerce_app.cart.dto.UpdatedCartInfoResponse;
import shopeazy.com.ecommerce_app.cart.enums.CartAction;
import shopeazy.com.ecommerce_app.cart.model.Cart;
import shopeazy.com.ecommerce_app.cart.model.pojo.CartItem;
import shopeazy.com.ecommerce_app.cart.repository.CartRepository;
import shopeazy.com.ecommerce_app.common.exception.ResourceNotFoundException;
import shopeazy.com.ecommerce_app.product.model.Product;
import shopeazy.com.ecommerce_app.product.repository.ProductRepository;
import shopeazy.com.ecommerce_app.product.service.ProductService;
import shopeazy.com.ecommerce_app.user.model.User;
import shopeazy.com.ecommerce_app.user.repository.UserRepository;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final ProductService productService;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public CartResponse addProductsToCart(AddProductsToCartRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(ResourceNotFoundException::new);


        User user = userRepository.findById(request.getUserId())
                .orElseThrow(ResourceNotFoundException::new);

        productService.checkProductAvailability(request.getProductId());
        productService.validateRequestedQuantity(request.getProductId(), request.getQuantity());

        // updating product stock
        product.setStockCount(product.getStockCount() - request.getQuantity());
        productRepository.save(product);


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


        return modelMapper.map(cart, CartResponse.class);

    }

    @Override
    @Transactional
    public UpdatedCartInfoResponse updateCartItems(UpdateCartRequest request, String usersEmail) {
        log.info("Updating cart for userEmail={}, request={}", usersEmail, request);

        // Validate quantity
        if (request.getQuantity() <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }

        // Validate product
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        // Validate user
        User user = userRepository.findByEmail(usersEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        log.info("Fetching cart for userId={}", user.getId());


        Cart cart = getCartOwnedBy(user);

        // Find product in cart
        Optional<CartItem> existingItemOpt = cart.getItems().stream()
                .filter(item -> item.getProductId().equals(request.getProductId()))
                .findFirst();

        CartAction action = Objects.requireNonNull(request.getAction(), "Action must not be null");

        switch (action) {
            case ADD -> {
                productService.validateRequestedQuantity(product.getId(), request.getQuantity());

                if (existingItemOpt.isPresent()) {
                    CartItem item = existingItemOpt.get();
                    item.setProductQuantity(item.getProductQuantity() + request.getQuantity());
                    log.info("Increased quantity of product {} in cart", product.getId());
                } else {
                    CartItem newItem = new CartItem();
                    newItem.setProductId(product.getId());
                    newItem.setProductName(product.getName());
                    newItem.setProductDescription(product.getDescription());
                    newItem.setProductPrice(BigDecimal.valueOf(product.getPrice()));
                    newItem.setProductQuantity(request.getQuantity());
                    cart.getItems().add(newItem);
                    log.info("Added new product {} to cart", product.getId());
                }

                product.setStockCount(product.getStockCount() - request.getQuantity());
                productRepository.save(product);
            }

            case REMOVE -> {
                if (existingItemOpt.isEmpty()) {
                    throw new IllegalArgumentException("Product not found in cart");
                }

                CartItem item = existingItemOpt.get();
                int currentQty = item.getProductQuantity();
                int removeQty = request.getQuantity();

                if (removeQty >= currentQty) {
                    cart.getItems().remove(item);
                    productService.restoreStock(product.getId(), currentQty);
                    log.info("Removed product {} completely from cart", product.getId());
                } else {
                    item.setProductQuantity(currentQty - removeQty);
                    productService.restoreStock(product.getId(), removeQty);
                    log.info("Reduced quantity of product {} in cart", product.getId());
                }
            }
        }

        cart.setUpdatedAt(Instant.now());
        cartRepository.save(cart);

        return modelMapper.map(cart, UpdatedCartInfoResponse.class);
    }
    @Override
    public CartResponse viewCart(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("No cart found for user"));

        return modelMapper.map(cart, CartResponse.class);
    }

    @Transactional
    @Override
    public void clearCart(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

        // Restore stock
        for (CartItem item : cart.getItems()) {
            productService.restoreStock(item.getProductId(), item.getProductQuantity());
        }

        cart.getItems().clear(); // empty cart
        cart.setUpdatedAt(Instant.now());

        cartRepository.save(cart); // persist the update
    }

    private Cart getCartOwnedBy(User user) {
        return cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("No cart found for the authenticated user"));
    }

}
