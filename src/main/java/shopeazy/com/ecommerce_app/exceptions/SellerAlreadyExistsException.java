package shopeazy.com.ecommerce_app.exceptions;

public class SellerAlreadyExistsException extends RuntimeException {
    public SellerAlreadyExistsException(String message) {
        super(message);
    }
}
