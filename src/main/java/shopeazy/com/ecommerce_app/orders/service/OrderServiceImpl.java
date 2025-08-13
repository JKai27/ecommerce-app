package shopeazy.com.ecommerce_app.orders.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shopeazy.com.ecommerce_app.orders.dto.CreateOrderRequest;
import shopeazy.com.ecommerce_app.orders.dto.OrderResponseDto;
import shopeazy.com.ecommerce_app.orders.repository.OrderRepository;
import shopeazy.com.ecommerce_app.product.repository.ProductRepository;
import shopeazy.com.ecommerce_app.seller.repository.SellerProfileRepository;
import shopeazy.com.ecommerce_app.shopping_cart.repository.CartRepository;
import shopeazy.com.ecommerce_app.user.repository.UserRepository;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final SellerProfileRepository sellerProfileRepository;

    @Override
    public OrderResponseDto createOrderFromCart(CreateOrderRequest request, String userEmail) {
        return null;
    }
}
