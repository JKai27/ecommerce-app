package shopeazy.com.ecommerce_app.user.exception;

import org.springframework.http.HttpStatus;
import shopeazy.com.ecommerce_app.common.exception.BusinessException;
import shopeazy.com.ecommerce_app.common.exception.ProblemTypes;

public class UserNotFoundException extends BusinessException {
    public UserNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, ProblemTypes.NOT_FOUND, message);
    }
}
