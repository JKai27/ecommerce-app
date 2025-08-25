package shopeazy.com.ecommerce_app.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

/**
 * Kafka configuration for my e-commerce-app order management system.
 * Defines all the topics used for event-driven architecture.
 */
@Configuration
public class KafkaConfig {

    // Topic names as constants
    public static final String ORDER_EVENTS_TOPIC = "order-event";
    public static final String INVENTORY_EVENTS_TOPIC = "inventory-event";
    public static final String NOTIFICATION_EVENTS_TOPIC = "notification-event";
    public static final String PAYMENT_EVENTS_TOPIC = "payment-event";
    public static final String CART_EVENTS_TOPIC = "cart-event";
    public static final String WISHLIST_EVENTS_TOPIC = "wishlist-event";

    @Bean
    public NewTopic orderEventsTopic() {
        return TopicBuilder.name(ORDER_EVENTS_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic inventoryEventsTopic() {
        return TopicBuilder.name(INVENTORY_EVENTS_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic notificationEventsTopic() {
        return TopicBuilder.name(NOTIFICATION_EVENTS_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic paymentEventsTopic() {
        return TopicBuilder.name(PAYMENT_EVENTS_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic cartEventsTopic() {
        return TopicBuilder.name(CART_EVENTS_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic wishlistEventsTopic() {
        return TopicBuilder.name(WISHLIST_EVENTS_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }
}
