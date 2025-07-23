package shopeazy.com.ecommerce_app.cart.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import shopeazy.com.ecommerce_app.cart.dto.AddProductsToCartRequest;
import shopeazy.com.ecommerce_app.cart.dto.CartResponse;
import shopeazy.com.ecommerce_app.cart.model.Cart;
import shopeazy.com.ecommerce_app.cart.repository.CartRepository;
import shopeazy.com.ecommerce_app.product.dto.ProductAvailabilityResponse;
import shopeazy.com.ecommerce_app.product.model.Product;
import shopeazy.com.ecommerce_app.product.repository.ProductRepository;
import shopeazy.com.ecommerce_app.product.service.ProductService;
import shopeazy.com.ecommerce_app.user.model.User;
import shopeazy.com.ecommerce_app.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CartServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProductService productService;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private CartServiceImpl cartService;

    @Test
    void testAddProductsToCart() {
        // Arrange
        Product product = new Product();
        product.setId("product123");
        product.setName("Test Product");
        product.setStockCount(10);
        product.setPrice(500.00);

        User user = new User();
        user.setId("user123");

        Cart cart = new Cart();
        cart.setUserId("user123");
        cart.setItems(new ArrayList<>());

        CartResponse expectedResponse = new CartResponse();

        AddProductsToCartRequest request = new AddProductsToCartRequest();
        request.setProductId("product123");
        request.setUserId("user123");
        request.setQuantity(2);

        ProductAvailabilityResponse mockResponse = new ProductAvailabilityResponse();
        mockResponse.setAvailable(true);
        mockResponse.setProductStockCount(10);
        mockResponse.setMessage("Product available");

        // Stubbing
        when(productRepository.findById("product123")).thenReturn(Optional.of(product));
        when(userRepository.findById("user123")).thenReturn(Optional.of(user));
        when(productService.checkProductAvailability("product123")).thenReturn(mockResponse);
        doNothing().when(productService).validateRequestedQuantity("product123", 2);
        when(cartRepository.findByUserId("user123")).thenReturn(Optional.of(cart));
        when(productRepository.save(any())).thenReturn(product);
        when(cartRepository.save(any())).thenReturn(cart);
        when(modelMapper.map(any(Cart.class), eq(CartResponse.class))).thenReturn(expectedResponse);

        // Act
        CartResponse actualResponse = cartService.addProductsToCart(request);

        // Assert
        assertEquals(expectedResponse, actualResponse);
    }
}
