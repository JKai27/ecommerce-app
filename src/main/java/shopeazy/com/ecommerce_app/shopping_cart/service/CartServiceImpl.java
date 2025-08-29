package shopeazy.com.ecommerce_app.shopping_cart.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shopeazy.com.ecommerce_app.shopping_cart.dto.AddProductsToCartRequest;
import shopeazy.com.ecommerce_app.shopping_cart.dto.CartResponse;
import shopeazy.com.ecommerce_app.shopping_cart.dto.UpdateCartRequest;
import shopeazy.com.ecommerce_app.shopping_cart.dto.UpdatedCartInfoResponse;
import shopeazy.com.ecommerce_app.shopping_cart.enums.CartAction;
import shopeazy.com.ecommerce_app.shopping_cart.exception.ProductNotInCartException;
import shopeazy.com.ecommerce_app.shopping_cart.model.Cart;
import shopeazy.com.ecommerce_app.shopping_cart.model.pojo.CartItem;
import shopeazy.com.ecommerce_app.shopping_cart.model.pojo.RemovedProductItem;
import shopeazy.com.ecommerce_app.shopping_cart.repository.CartRepository;
import shopeazy.com.ecommerce_app.common.exception.ResourceNotFoundException;
import shopeazy.com.ecommerce_app.common.exception.BusinessException;
import shopeazy.com.ecommerce_app.common.exception.ProblemTypes;
import org.springframework.http.HttpStatus;
import shopeazy.com.ecommerce_app.product.dto.ProductAvailabilityResponse;
import shopeazy.com.ecommerce_app.product.model.Product;
import shopeazy.com.ecommerce_app.product.repository.ProductRepository;
import shopeazy.com.ecommerce_app.product.service.ProductService;
import shopeazy.com.ecommerce_app.user.model.User;
import shopeazy.com.ecommerce_app.user.repository.UserRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
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
    private static final String MISSING_USER_IN_DB = "User not found";

    @Override
    @Transactional
    public CartResponse addProductsToCart(AddProductsToCartRequest request, String userEmail) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(ResourceNotFoundException::new);


        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(ResourceNotFoundException::new);

        ProductAvailabilityResponse response = productService.checkProductAvailability(request.getProductId());
        log.info("Product availability response and count of stock: {}, {}", response.isAvailable(), response.getProductStockCount());
        productService.validateRequestedQuantity(request.getProductId(), request.getQuantity());

        // updating product stock
        log.info("Product count before update: {}", product.getStockCount());
        log.info("Product count update request: {}", request.getQuantity());

        product.setStockCount(product.getStockCount() - request.getQuantity());
        log.info("Product count after update: {}", product.getStockCount());

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
            CartItem newItem = createCartItem(product, request.getQuantity());
            cart.getItems().add(newItem);
        }

        cart.setUpdatedAt(Instant.now());
        cartRepository.save(cart);
        CartResponse cartResponse = modelMapper.map(cart, CartResponse.class);
        cartResponse.setUserEmail(user.getEmail());
        return cartResponse;


    }

    @Override
    @Transactional
    public UpdatedCartInfoResponse updateCartItems(UpdateCartRequest request, String userEmail) {
        log.info("Updating cart for userEmail={}, request={}", userEmail, request);

        if (request.getQuantity() <= 0) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, ProblemTypes.INVALID_QUANTITY, "Quantity must be greater than zero");
        }

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException(MISSING_USER_IN_DB));

        Cart cart = getCartOwnedBy(user);

        Optional<CartItem> existingItemOpt = cart.getItems().stream()
                .filter(item -> item.getProductId().equals(request.getProductId()))
                .findFirst();

        CartAction action = Objects.requireNonNull(request.getAction(), "Action must not be null");

        UpdatedCartInfoResponse response = new UpdatedCartInfoResponse();
        List<RemovedProductItem> removed = new ArrayList<>();

        switch (action) {
            case ADD -> {
                productService.validateRequestedQuantity(product.getId(), request.getQuantity());

                if (existingItemOpt.isPresent()) {
                    CartItem item = existingItemOpt.get();
                    item.setProductQuantity(item.getProductQuantity() + request.getQuantity());
                    log.info("Increased quantity of product {} in cart", product.getId());
                } else {
                    CartItem newItem = createCartItem(product, request.getQuantity());
                    cart.getItems().add(newItem);
                    log.info("Added new product {} to cart", product.getId());
                }

                product.setStockCount(product.getStockCount() - request.getQuantity());
                productRepository.save(product);
            }
            case REMOVE -> {
                if (existingItemOpt.isEmpty()) {
                    log.warn("User {} tried to remove product {} not in cart", user.getEmail(), request.getProductId());
                    throw new ProductNotInCartException("The cart is already empty.");
                }

                CartItem item = existingItemOpt.get();

                if (item.getProductQuantity() == 0) {
                    log.warn("Attempted to remove product {} from cart, but quantity is already 0", product.getId());
                    throw new IllegalStateException("Product quantity is already 0 in the cart");
                }

                int currentQty = item.getProductQuantity();
                int removeQty = request.getQuantity();

                RemovedProductItem removedItem = new RemovedProductItem();
                removedItem.setProductId(product.getId());

                if (removeQty >= currentQty) {
                    cart.getItems().remove(item);
                    removedItem.setQuantity(currentQty);
                    productService.restoreStock(product.getId(), currentQty);
                    log.info("Removed product {} completely from cart", product.getId());
                } else {
                    item.setProductQuantity(currentQty - removeQty);
                    removedItem.setQuantity(removeQty);
                    productService.restoreStock(product.getId(), removeQty);
                    log.info("Reduced quantity of product {} in cart", product.getId());
                }

                removed.add(removedItem);
            }

        }

        cart.setUpdatedAt(Instant.now());
        cartRepository.save(cart);

        CartResponse cartResponse = modelMapper.map(cart, CartResponse.class);
        cartResponse.setUserEmail(user.getEmail());

        response.setCart(cartResponse);
        response.setRemovedProducts(removed);

        return response;
    }

    @Override
    public CartResponse viewCart(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException(MISSING_USER_IN_DB));

        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("No cart found for user"));

        return modelMapper.map(cart, CartResponse.class);
    }

    @Transactional
    @Override
    public void clearCart(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException(MISSING_USER_IN_DB));

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

    /**
     * Helper method to create CartItem with proper pricing calculations
     */
    private CartItem createCartItem(Product product, int quantity) {
        CartItem newItem = new CartItem();
        newItem.setProductId(product.getId());
        newItem.setProductName(product.getName());
        newItem.setProductDescription(product.getDescription());
        newItem.setProductQuantity(quantity);
        
        // Calculate pricing
        BigDecimal originalPrice = BigDecimal.valueOf(product.getPrice());
        BigDecimal discountPercentage = BigDecimal.valueOf(product.getDiscount());
        BigDecimal discountAmount = originalPrice.multiply(discountPercentage)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        BigDecimal discountedPrice = originalPrice.subtract(discountAmount);
        
        newItem.setOriginalPrice(originalPrice);
        newItem.setDiscount(discountPercentage);
        newItem.setDiscountedPrice(discountedPrice);
        
        return newItem;
    }

}
