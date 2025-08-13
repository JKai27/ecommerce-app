package shopeazy.com.ecommerce_app.product.exception;

import org.springframework.http.HttpStatus;
import shopeazy.com.ecommerce_app.common.exception.BusinessException;
import shopeazy.com.ecommerce_app.common.exception.ProblemTypes;

public class InvalidStatusException extends BusinessException {
    public InvalidStatusException(String message) {
        super(HttpStatus.BAD_REQUEST, ProblemTypes.BAD_REQUEST, message);
    }
}
