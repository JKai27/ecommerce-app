package shopeazy.com.ecommerce_app.security.exception;

import org.springframework.http.HttpStatus;
import shopeazy.com.ecommerce_app.common.exception.BusinessException;
import shopeazy.com.ecommerce_app.common.exception.ProblemTypes;

public class ForbiddenOperationException extends BusinessException {
    public ForbiddenOperationException(String message) {
        super(HttpStatus.FORBIDDEN, ProblemTypes.FORBIDDEN_OPERATION, message);
    }
}
