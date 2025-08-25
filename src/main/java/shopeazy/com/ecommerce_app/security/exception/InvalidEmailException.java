package shopeazy.com.ecommerce_app.security.exception;

import org.springframework.http.HttpStatus;
import shopeazy.com.ecommerce_app.common.exception.BusinessException;
import shopeazy.com.ecommerce_app.common.exception.ProblemTypes;

/**
 * Thrown when an email address format is invalid or email-related operations fail
 * <p>
 * Example usage:
 * throw new InvalidEmailException("Invalid email format")
 * .withProperty("email", invalidEmailValue);
 */
public class InvalidEmailException extends BusinessException {

    public InvalidEmailException(String message) {
        super(HttpStatus.BAD_REQUEST, ProblemTypes.INVALID_EMAIL, message);
    }

    public InvalidEmailException(String message, String email) {
        super(HttpStatus.BAD_REQUEST, ProblemTypes.INVALID_EMAIL, message);
        withProperty("email", email);
    }
}
