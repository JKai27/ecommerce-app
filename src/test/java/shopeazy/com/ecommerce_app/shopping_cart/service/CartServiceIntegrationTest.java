package shopeazy.com.ecommerce_app.shopping_cart.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import shopeazy.com.ecommerce_app.shopping_cart.dto.AddProductsToCartRequest;
import shopeazy.com.ecommerce_app.shopping_cart.dto.CartResponse;
import shopeazy.com.ecommerce_app.shopping_cart.repository.CartRepository;
import shopeazy.com.ecommerce_app.product.model.Product;
import shopeazy.com.ecommerce_app.product.repository.ProductRepository;
import shopeazy.com.ecommerce_app.user.model.User;
import shopeazy.com.ecommerce_app.user.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@AutoConfigureDataMongo
@TestPropertySource(locations = "classpath:application-test.properties")
public class CartServiceIntegrationTest {
    @Autowired
    private CartService cartService;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CartRepository cartRepository;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();
        userRepository.deleteAll();
        cartRepository.deleteAll();

        Product product = new Product();
        product.setId("product123");
        product.setName("Test-Product");
        product.setStockCount(20);
        product.setDescription("Desc");
        product.setPrice(30.0);
        productRepository.save(product);

        User user = new User();
        user.setId("user123");
        user.setEmail("email123");
        userRepository.save(user);
    }

    @Test
    void testAddToCart_Integration() {
        AddProductsToCartRequest request = new AddProductsToCartRequest();
        request.setProductId("product123");
        request.setQuantity(2);

        String userEmail = "email123";

        CartResponse response = cartService.addProductsToCart(request, userEmail);

        assertNotNull(response);
        assertEquals(userEmail, response.getUserEmail());
        assertEquals(1, response.getItems().size());
        assertEquals("product123", response.getItems().get(0).getProductId());
        assertEquals(2, response.getItems().get(0).getProductQuantity());
    }
}
