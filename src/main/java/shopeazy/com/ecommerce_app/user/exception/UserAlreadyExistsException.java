package shopeazy.com.ecommerce_app.user.exception;

import org.springframework.http.HttpStatus;
import shopeazy.com.ecommerce_app.common.exception.BusinessException;
import shopeazy.com.ecommerce_app.common.exception.ProblemTypes;

public class UserAlreadyExistsException extends BusinessException {
    public UserAlreadyExistsException(String message) {
        super(HttpStatus.CONFLICT, ProblemTypes.CONFLICT, message);
    }
}
