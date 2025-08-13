package shopeazy.com.ecommerce_app.user.exception;

import org.springframework.http.HttpStatus;
import shopeazy.com.ecommerce_app.common.exception.BusinessException;

import static shopeazy.com.ecommerce_app.common.exception.ProblemTypes.BAD_REQUEST;

public class PasswordDoesNotMatchException extends BusinessException {
    public PasswordDoesNotMatchException(String userId) {

        super(HttpStatus.BAD_REQUEST, BAD_REQUEST, "The provided password does not match your current password.");
        withProperty("userId", userId);
    }
}
