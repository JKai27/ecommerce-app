package shopeazy.com.ecommerce_app.admin.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StatusUpdateRequest {
    @NotNull(message = "Status must not be null")
    private String status;
}
