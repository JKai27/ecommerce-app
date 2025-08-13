package shopeazy.com.ecommerce_app.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

/**
 * Optional Email configuration that provides mock implementations when email is disabled
 * 
 * This prevents email-related startup failures when MailHog is not available.
 */
@Slf4j
@Configuration
public class EmailOptionalConfig {

    @Value("${app.features.email.enabled:true}")
    private boolean emailEnabled;

    /**
     * JavaMailSender that adapts based on whether email is enabled
     * When email is disabled, it provides a mock implementation that logs to console
     */
    @Bean
    @ConditionalOnMissingBean
    public JavaMailSender javaMailSender() {
        if (emailEnabled) {
            log.info("📧 Email is enabled but no JavaMailSender bean found. This should be provided by Spring Boot Mail auto-configuration.");
            // This shouldn't happen in normal cases since Spring Boot auto-configuration should provide the bean
            // But just in case, return a simple mock
        } else {
            log.info("📧 Creating mock JavaMailSender (emails will be logged to console)");
        }
        
        return new JavaMailSender() {
            @Override
            public void send(SimpleMailMessage simpleMessage) {
                log.info("📧 [MOCK EMAIL] To: {} | Subject: {} | Text: {}", 
                    simpleMessage.getTo(), simpleMessage.getSubject(), simpleMessage.getText());
            }

            @Override
            public void send(SimpleMailMessage... simpleMessages) {
                for (SimpleMailMessage msg : simpleMessages) {
                    send(msg);
                }
            }

            @Override
            public jakarta.mail.internet.MimeMessage createMimeMessage() {
                // Return a minimal mock MimeMessage implementation
                try {
                    return new jakarta.mail.internet.MimeMessage((jakarta.mail.Session) null);
                } catch (Exception e) {
                    log.warn("Could not create mock MimeMessage: {}", e.getMessage());
                    return null;
                }
            }

            @Override
            public jakarta.mail.internet.MimeMessage createMimeMessage(java.io.InputStream contentStream) {
                return createMimeMessage();
            }

            @Override
            public void send(jakarta.mail.internet.MimeMessage mimeMessage) {
                try {
                    log.info("📧 [MOCK EMAIL] MimeMessage sent: Subject = {}", 
                        mimeMessage.getSubject() != null ? mimeMessage.getSubject() : "No Subject");
                } catch (Exception e) {
                    log.info("📧 [MOCK EMAIL] MimeMessage sent (could not parse details)");
                }
            }

            @Override
            public void send(jakarta.mail.internet.MimeMessage... mimeMessages) {
                for (jakarta.mail.internet.MimeMessage msg : mimeMessages) {
                    send(msg);
                }
            }

            @Override
            public void send(org.springframework.mail.javamail.MimeMessagePreparator mimeMessagePreparator) {
                log.info("📧 [MOCK EMAIL] MimeMessagePreparator executed");
            }

            @Override
            public void send(org.springframework.mail.javamail.MimeMessagePreparator... mimeMessagePreparators) {
                for (org.springframework.mail.javamail.MimeMessagePreparator preparator : mimeMessagePreparators) {
                    send(preparator);
                }
            }
        };
    }
}