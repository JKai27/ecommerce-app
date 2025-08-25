package shopeazy.com.ecommerce_app.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

import java.util.Properties;

/**
 * Custom condition that checks if Kafka is both enabled and available
 */
@Slf4j
public class KafkaAvailableCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        // First check if Kafka is enabled
        String kafkaEnabled = context.getEnvironment().getProperty("app.features.kafka.enabled", "false");
        if (!"true".equals(kafkaEnabled)) {
            log.debug("Kafka is disabled via app.features.kafka.enabled=false");
            return false;
        }

        // Then check if Kafka is actually available
        String bootstrapServers = context.getEnvironment().getProperty("spring.kafka.bootstrap-servers", "localhost:9092");
        
        log.info("🔍 Checking Kafka availability at: {}", bootstrapServers);
        
        Properties props = new Properties();
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(AdminClientConfig.REQUEST_TIMEOUT_MS_CONFIG, 2000);
        props.put(AdminClientConfig.DEFAULT_API_TIMEOUT_MS_CONFIG, 3000);
        
        try (AdminClient adminClient = AdminClient.create(props)) {
            // Try to fetch cluster metadata - this will timeout if Kafka is not available
            adminClient.describeCluster().clusterId().get(3, java.util.concurrent.TimeUnit.SECONDS);
            log.info("✅ Kafka is available at: {}", bootstrapServers);
            return true;
        } catch (Exception e) {
            log.warn("❌ Kafka is not available at: {}. Error: {}", bootstrapServers, e.getMessage());
            log.warn("🔄 Kafka configuration will be skipped. Application will continue without Kafka.");
            return false;
        }
    }
}