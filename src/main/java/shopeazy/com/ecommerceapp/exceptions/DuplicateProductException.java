package shopeazy.com.ecommerceapp.exceptions;

public class DuplicateProductException extends RuntimeException {

    public DuplicateProductException(String message) {
        super(message);
    }
}