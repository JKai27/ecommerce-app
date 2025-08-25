package shopeazy.com.ecommerce_app.shopping_cart.exception;

import org.springframework.http.HttpStatus;
import shopeazy.com.ecommerce_app.common.exception.BusinessException;
import shopeazy.com.ecommerce_app.common.exception.ProblemTypes;

public class ProductNotInCartException extends BusinessException {
    public ProductNotInCartException(String message) {
        super(HttpStatus.BAD_REQUEST, ProblemTypes.PRODUCT_NOT_IN_CART, message);
    }
}