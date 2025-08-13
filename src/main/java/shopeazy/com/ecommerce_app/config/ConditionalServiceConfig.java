package shopeazy.com.ecommerce_app.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.MailSender;
import org.springframework.mail.javamail.JavaMailSender;

/**
 * Conditional configuration for external services
 * 
 * This allows the application to start even when some services are unavailable
 * by providing fallback implementations or disabling features entirely.
 */
@Slf4j
@Configuration
public class ConditionalServiceConfig {
    
    /**
     * Mock Kafka service for when Kafka is disabled
     * Logs messages instead of sending to Kafka
     */
    @Configuration
    @ConditionalOnProperty(name = "app.features.kafka.enabled", havingValue = "false", matchIfMissing = false)
    static class MockKafkaConfig {
        
        @Bean
        public MockKafkaService mockKafkaService() {
            log.info("🔧 Kafka is disabled - using mock implementation");
            return new MockKafkaService();
        }
    }
    
    /**
     * Mock Email service for when Email is disabled  
     * Logs emails instead of sending them
     */
    @Configuration
    @ConditionalOnProperty(name = "app.features.email.enabled", havingValue = "false", matchIfMissing = false)
    static class MockEmailConfig {
        
        @Bean
        public MockEmailService mockEmailService() {
            log.info("📧 Email is disabled - using console logging implementation");
            return new MockEmailService();
        }
    }
    
    /**
     * Mock Redis service for when Redis is disabled
     * Uses in-memory storage instead of Redis
     */
    @Configuration  
    @ConditionalOnProperty(name = "app.features.redis.enabled", havingValue = "false", matchIfMissing = false)
    static class MockRedisConfig {
        
        @Bean
        public MockRedisService mockRedisService() {
            log.info("📦 Redis is disabled - using in-memory storage");
            return new MockRedisService();
        }
    }
}

/**
 * Mock Kafka service that logs messages instead of sending to Kafka
 */
@Slf4j  
class MockKafkaService {
    
    public void sendMessage(String topic, Object message) {
        log.info("🎯 [MOCK KAFKA] Would send to topic '{}': {}", topic, message);
    }
    
    public void sendOrderEvent(String orderId, String status) {
        log.info("📦 [MOCK KAFKA] Order Event - ID: {}, Status: {}", orderId, status);
    }
    
    public void sendUserEvent(String userId, String event) {
        log.info("👤 [MOCK KAFKA] User Event - ID: {}, Event: {}", userId, event);
    }
}

/**
 * Mock Email service that logs emails instead of sending them
 */
@Slf4j
class MockEmailService {
    
    public void sendEmail(String to, String subject, String body) {
        log.info("📧 [MOCK EMAIL] To: {} | Subject: {} | Body: {}", to, subject, body);
        log.info("💡 In production, this would be sent via SMTP");
    }
    
    public void sendWelcomeEmail(String userEmail, String userName) {
        log.info("🎉 [MOCK EMAIL] Welcome email to {} ({})", userName, userEmail);
    }
    
    public void sendOrderConfirmation(String userEmail, String orderId) {
        log.info("📋 [MOCK EMAIL] Order confirmation to {} for order {}", userEmail, orderId);
    }
}

/**
 * Mock Redis service that uses in-memory storage instead of Redis
 */
@Slf4j
class MockRedisService {
    
    private final java.util.concurrent.ConcurrentHashMap<String, Object> inMemoryStore = 
        new java.util.concurrent.ConcurrentHashMap<>();
    
    public void set(String key, Object value) {
        inMemoryStore.put(key, value);
        log.debug("📦 [MOCK REDIS] SET {} = {}", key, value);
    }
    
    public Object get(String key) {
        Object value = inMemoryStore.get(key);
        log.debug("📦 [MOCK REDIS] GET {} = {}", key, value);
        return value;
    }
    
    public void delete(String key) {
        inMemoryStore.remove(key);
        log.debug("📦 [MOCK REDIS] DELETE {}", key);
    }
    
    public boolean exists(String key) {
        boolean exists = inMemoryStore.containsKey(key);
        log.debug("📦 [MOCK REDIS] EXISTS {} = {}", key, exists);
        return exists;
    }
}