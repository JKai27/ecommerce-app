package shopeazy.com.ecommerce_app.exceptions;

public class DuplicateProductException extends RuntimeException {

    public DuplicateProductException(String message) {
        super(message);
    }
}