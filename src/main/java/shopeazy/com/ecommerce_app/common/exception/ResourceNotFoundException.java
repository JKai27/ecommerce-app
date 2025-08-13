package shopeazy.com.ecommerce_app.common.exception;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends BusinessException {
    public ResourceNotFoundException() {
        super(HttpStatus.NOT_FOUND, ProblemTypes.NOT_FOUND, "Resource not found");
    }
    
    public ResourceNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, ProblemTypes.NOT_FOUND, message);
    }
}
