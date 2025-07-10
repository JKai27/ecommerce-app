package shopeazy.com.ecommerce_app.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class UserUpdateInBulkRequest {
    @NotNull
    @Size(min = 1)
    private List<@NotBlank String> userIDs;
}
