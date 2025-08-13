package shopeazy.com.ecommerce_app.seller.exception;

import org.springframework.http.HttpStatus;
import shopeazy.com.ecommerce_app.common.exception.BusinessException;

public class SellerAlreadyExistsException extends BusinessException {
    public SellerAlreadyExistsException(String message) {
        super(HttpStatus.CONFLICT,"Seller Already Exists", message);
    }
}
