package shopeazy.com.ecommerce_app.model.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class IdListRequest {
    private List<@NotNull String> ids;
}