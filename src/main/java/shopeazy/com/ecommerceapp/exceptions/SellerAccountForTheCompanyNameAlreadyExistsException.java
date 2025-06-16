package shopeazy.com.ecommerceapp.exceptions;

public class SellerAccountForTheCompanyNameAlreadyExistsException extends RuntimeException {

    public SellerAccountForTheCompanyNameAlreadyExistsException(String message) {
        super(message);
    }

    public SellerAccountForTheCompanyNameAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}