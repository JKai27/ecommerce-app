package shopeazy.com.ecommerce_app.shopping_cart.exception;

public class ProductNotInCartException extends RuntimeException {
    public ProductNotInCartException(String message) {
        super(message);
    }
}