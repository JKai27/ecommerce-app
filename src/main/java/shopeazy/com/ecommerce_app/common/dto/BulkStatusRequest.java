package shopeazy.com.ecommerce_app.common.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;
@Data
public class BulkStatusRequest {

    @NotNull
    @Size(min = 1)
    private List<String> ids;

    @NotNull
    @Pattern(regexp = "ACTIVE|BLOCKED", flags = Pattern.Flag.CASE_INSENSITIVE, message = "Status must be either ACTIVE or BLOCKED")
    private String status;
}