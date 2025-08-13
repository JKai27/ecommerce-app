package shopeazy.com.ecommerce_app.common.exception;

/**
 * RFC 7807 Problem Detail Type URIs
 * 
 * These are stable identifiers that help clients identify error types programmatically.
 * They don't need to be actual working URLs, just stable identifiers.
 * 
 * Best Practice: Use your domain + /problems/ + descriptive-slug
 */
public final class ProblemTypes {
    
    private static final String BASE_URI = "https://api.shopeazy.com/problems/";
    
    // 4xx Client Errors
    public static final String BAD_REQUEST = BASE_URI + "bad-request";
    public static final String VALIDATION_ERROR = BASE_URI + "validation-error";
    public static final String CONSTRAINT_VIOLATION = BASE_URI + "constraint-violation";
    public static final String UNAUTHORIZED = BASE_URI + "unauthorized";
    public static final String FORBIDDEN = BASE_URI + "forbidden";
    public static final String NOT_FOUND = BASE_URI + "not-found";
    public static final String CONFLICT = BASE_URI + "conflict";
    
    // Domain-specific 4xx errors
    public static final String INVALID_EMAIL = BASE_URI + "invalid-email";
    public static final String DUPLICATE_PRODUCT = BASE_URI + "duplicate-product";
    public static final String PRODUCT_OUT_OF_STOCK = BASE_URI + "product-out-of-stock";
    public static final String PRODUCT_NOT_IN_CART = BASE_URI + "product-not-in-cart";
    public static final String SELLER_ALREADY_EXISTS = BASE_URI + "seller-already-exists";
    public static final String COMPANY_NAME_TAKEN = BASE_URI + "company-name-taken";
    public static final String FORBIDDEN_OPERATION = BASE_URI + "forbidden-operation";
    public static final String INVALID_QUANTITY = BASE_URI + "invalid-quantity";
    public static final String UNAUTHENTICATED_ACCESS = BASE_URI + "unauthenticated-access";
    public static final String INVALID_REQUEST_DATA = BASE_URI + "invalid-request-data";
    
    // 5xx Server Errors
    public static final String INTERNAL_ERROR = BASE_URI + "internal-server-error";
    public static final String SERVICE_UNAVAILABLE = BASE_URI + "service-unavailable";
    
    // Prevent instantiation
    private ProblemTypes() {}
}