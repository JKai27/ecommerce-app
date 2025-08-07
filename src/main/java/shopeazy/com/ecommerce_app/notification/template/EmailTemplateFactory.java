package shopeazy.com.ecommerce_app.notification.template;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class EmailTemplateFactory {

    private final List<EmailTemplateBuilder> builders;

    public EmailTemplateBuilder getTemplate(String status) {
        if (status == null || status.trim().isEmpty()) {
            throw new IllegalArgumentException("Status cannot be null or empty");
        }
        List<EmailTemplateBuilder> matchingBuilders = builders.stream()
                .filter(builder-> builder.supports(status))
                .toList();

        if (matchingBuilders.isEmpty()) {
            throw new IllegalArgumentException("No template found for status: " + status);
        }
        if (matchingBuilders.size() > 1) {
            throw new IllegalStateException(
                    String.format("Multiple templates found for status '%s': %s. Only one template per status is allowed.",
                            status, matchingBuilders.stream().map(b -> b.getClass().getSimpleName()).collect(Collectors.toList()))
            );
        }

        return matchingBuilders.get(0);
    }
}
