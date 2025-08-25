package shopeazy.com.ecommerce_app.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;
@Getter
public class BusinessException extends RuntimeException {
    private final HttpStatus httpStatus;
    private final String typeUri;
    private final Map<String, Object> properties = new HashMap<>();

    public BusinessException(HttpStatus httpStatus, String typeUri) {
        this.httpStatus = httpStatus;
        this.typeUri = typeUri;
    }

    public BusinessException(HttpStatus httpStatus, String typeUri, String message) {
        super(message);
        this.httpStatus = httpStatus;
        this.typeUri = typeUri;
    }

    public BusinessException(HttpStatus httpStatus, String typeUri, String message, Throwable cause) {
        super(message, cause);
        this.httpStatus = httpStatus;
        this.typeUri = typeUri;
    }

    public BusinessException withProperty(String key, Object value) {
        this.properties.put(key, value);
        return this;
    }

}
