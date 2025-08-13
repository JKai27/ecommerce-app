package shopeazy.com.ecommerce_app.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@Deprecated // Use BusinessException directly instead
public class AppException extends BusinessException {
    private final HttpStatus status;

    public AppException(HttpStatus status, String message) {
        super(status, ProblemTypes.BAD_REQUEST, message);
        this.status = status;
    }
}
