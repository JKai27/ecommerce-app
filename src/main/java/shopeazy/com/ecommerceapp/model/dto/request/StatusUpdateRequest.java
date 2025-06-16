package shopeazy.com.ecommerceapp.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class StatusUpdateRequest {
    @NotBlank
    private String status;
}
