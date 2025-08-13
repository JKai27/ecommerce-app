package shopeazy.com.ecommerce_app.product.exception;

import org.springframework.http.HttpStatus;
import shopeazy.com.ecommerce_app.common.exception.BusinessException;
import shopeazy.com.ecommerce_app.common.exception.ProblemTypes;

public class DuplicateProductException extends BusinessException {

    public DuplicateProductException(String message) {
        super(HttpStatus.CONFLICT, ProblemTypes.DUPLICATE_PRODUCT, message);
    }
}