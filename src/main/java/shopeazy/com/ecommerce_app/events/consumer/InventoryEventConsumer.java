package shopeazy.com.ecommerce_app.events.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import shopeazy.com.ecommerce_app.notification.service.EmailNotificationService;
import shopeazy.com.ecommerce_app.product.repository.ProductRepository;
import shopeazy.com.ecommerce_app.seller.repository.SellerProfileRepository;
import shopeazy.com.ecommerce_app.user.repository.UserRepository;


@Slf4j
@Component
@RequiredArgsConstructor
public class InventoryEventConsumer {

    private final ProductRepository productRepository;
    private final SellerProfileRepository sellerProfileRepository;
    private final UserRepository userRepository;
    private final EmailNotificationService emailService;
}
