package shopeazy.com.ecommerce_app.product.exception;

import org.springframework.http.HttpStatus;
import shopeazy.com.ecommerce_app.common.exception.BusinessException;
import shopeazy.com.ecommerce_app.common.exception.ProblemTypes;

public class ProductOutOfStockException extends BusinessException {
    public ProductOutOfStockException(String message) {
        super(HttpStatus.BAD_REQUEST, ProblemTypes.PRODUCT_OUT_OF_STOCK, message);
    }
}