package shopeazy.com.ecommerce_app.common.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class IdListRequest {
    private List<@NotNull String> ids;
}