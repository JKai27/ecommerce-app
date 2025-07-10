package shopeazy.com.ecommerce_app.exceptions;

public class PasswordDoesNotMatchException extends RuntimeException {
    public PasswordDoesNotMatchException(String message) {
        super(message);
    }
}
