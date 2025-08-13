package shopeazy.com.ecommerce_app.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

import jakarta.annotation.PostConstruct;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Configuration for monitoring external service health
 * 
 * This helps the application start gracefully even when some services are unavailable
 * and provides clear feedback about which services are reachable.
 */
@Slf4j
@Configuration
public class ServiceHealthConfig {
    
    @Value("${spring.data.mongodb.uri:mongodb://localhost:27017}")
    private String mongoUri;
    
    @Value("${spring.data.redis.host:localhost}")
    private String redisHost;
    
    @Value("${spring.data.redis.port:6379}")
    private int redisPort;
    
    @Value("${spring.kafka.bootstrap-servers:localhost:9092}")
    private String kafkaServers;
    
    @Value("${spring.mail.host:localhost}")
    private String mailHost;
    
    @Value("${spring.mail.port:1025}")
    private int mailPort;
    
    @PostConstruct
    public void checkServiceHealth() {
        log.info("🔍 Checking external service connectivity...");
        
        // Check Redis
        boolean redisAvailable = checkPortConnectivity(redisHost, redisPort, "Redis");
        
        // Check Kafka
        String[] kafkaHosts = kafkaServers.split(",");
        boolean kafkaAvailable = false;
        for (String kafkaHost : kafkaHosts) {
            String[] hostPort = kafkaHost.trim().split(":");
            String host = hostPort[0];
            int port = hostPort.length > 1 ? Integer.parseInt(hostPort[1]) : 9092;
            
            if (checkPortConnectivity(host, port, "Kafka")) {
                kafkaAvailable = true;
                break;
            }
        }
        
        // Check MailHog
        boolean mailHogAvailable = checkPortConnectivity(mailHost, mailPort, "MailHog");
        
        // Log summary
        log.info("📊 Service Connectivity Summary:");
        log.info("   Redis:   {}", redisAvailable ? "✅ Available" : "❌ Unavailable");
        log.info("   Kafka:   {}", kafkaAvailable ? "✅ Available" : "❌ Unavailable");
        log.info("   MailHog: {}", mailHogAvailable ? "✅ Available" : "❌ Unavailable");
        
        if (!redisAvailable || !kafkaAvailable || !mailHogAvailable) {
            log.warn("⚠️ Some external services are unavailable. The application will continue but some features may not work.");
            log.info("💡 To start all services, run: ./start-services.sh");
        } else {
            log.info("🎉 All external services are available!");
        }
    }
    
    private boolean checkPortConnectivity(String host, int port, String serviceName) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), 3000); // 3 second timeout
            log.debug("✅ {} is reachable at {}:{}", serviceName, host, port);
            return true;
        } catch (Exception e) {
            log.warn("❌ {} is not reachable at {}:{} - {}", serviceName, host, port, e.getMessage());
            return false;
        }
    }
}