package shopeazy.com.ecommerce_app.seller.exception;

import org.springframework.http.HttpStatus;
import shopeazy.com.ecommerce_app.common.exception.BusinessException;
import shopeazy.com.ecommerce_app.common.exception.ProblemTypes;

public class SellerAccountForTheCompanyNameAlreadyExistsException extends BusinessException {

    public SellerAccountForTheCompanyNameAlreadyExistsException(String message) {
        super(HttpStatus.CONFLICT, ProblemTypes.COMPANY_NAME_TAKEN, message);
    }

    public SellerAccountForTheCompanyNameAlreadyExistsException(String message, Throwable cause) {
        super(HttpStatus.CONFLICT, ProblemTypes.COMPANY_NAME_TAKEN, message, cause);
    }
}