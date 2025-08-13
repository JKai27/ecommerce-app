package shopeazy.com.ecommerce_app.common.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import shopeazy.com.ecommerce_app.product.exception.ProductOutOfStockException;
import shopeazy.com.ecommerce_app.security.exception.InvalidEmailException;
import shopeazy.com.ecommerce_app.seller.exception.SellerAccountForTheCompanyNameAlreadyExistsException;
import shopeazy.com.ecommerce_app.security.exception.ForbiddenOperationException;
import shopeazy.com.ecommerce_app.product.exception.DuplicateProductException;
import shopeazy.com.ecommerce_app.seller.exception.SellerAlreadyExistsException;
import shopeazy.com.ecommerce_app.shopping_cart.exception.ProductNotInCartException;

import java.net.URI;
import java.time.Instant;
import java.util.*;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
    /* ---------- Helper ---------- */

    private ResponseEntity<ProblemDetail> buildProblem(
            HttpStatus status,
            String title,
            String detail,
            String typeUri,
            HttpServletRequest request,
            Map<String, Object> extraProperties,
            Throwable exception
    ) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, Optional.ofNullable(detail).orElse(status.getReasonPhrase()));
        problemDetail.setTitle(title != null ? title : status.getReasonPhrase());
        if (typeUri != null) problemDetail.setType(URI.create(typeUri));

        try {
            problemDetail.setInstance(URI.create(request.getRequestURI()));
        } catch (Throwable throwable) {
            problemDetail.setProperty("instance", request.getRequestURI());
        }

        // timestamp + correlationId
        problemDetail.setProperty("timestamp", Instant.now());
        String correlationId = Optional.ofNullable(request.getHeader("X-Correlation-Id"))
                .filter(header -> !header.isBlank())
                .orElse(UUID.randomUUID().toString());
        problemDetail.setProperty("correlationId", correlationId);

        if (extraProperties != null) extraProperties.forEach(problemDetail::setProperty);

        // Logging: WARN bei 4xx, ERROR bei 5xx
        if (status.is4xxClientError()) {
            log.warn("[{}] {} {} -> {} {} | detail={}",
                    correlationId, request.getMethod(), request.getRequestURI(), status.value(), title, detail);
        } else if (status.is5xxServerError()) {
            log.error("[{}] {} {} -> {} {} | detail={}",
                    correlationId, request.getMethod(), request.getRequestURI(), status.value(), title, detail, exception);
        }
        return ResponseEntity.status(status).body(problemDetail);
    }

    /* ------------ Generic: BusinessException ------------- */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ProblemDetail> handleBusiness(BusinessException exception, HttpServletRequest request) {
        return buildProblem(
                exception.getHttpStatus(),
                exception.getHttpStatus().getReasonPhrase(),
                exception.getMessage(),
                exception.getTypeUri(),
                request,
                exception.getProperties(),
                exception
        );
    }

    /* ------------ Validation Errors ------------- */
    
    /**
     * Handles @Valid annotation failures on request body fields
     * Returns structured field-level validation errors
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleValidationErrors(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        
        BindingResult bindingResult = ex.getBindingResult();
        Map<String, List<String>> fieldErrors = new HashMap<>();

        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            String field = fieldError.getField();
            String errorMessage = fieldError.getDefaultMessage();
            fieldErrors.computeIfAbsent(field, k -> new ArrayList<>()).add(errorMessage);
        }

        Map<String, Object> properties = new HashMap<>();
        properties.put("errors", fieldErrors);

        return buildProblem(
            HttpStatus.BAD_REQUEST,
            "Validation Failed",
            "Request validation failed on " + fieldErrors.size() + " field(s)",
            ProblemTypes.VALIDATION_ERROR,
            request,
            properties,
            ex
        );
    }

    /**
     * Handles constraint violations from @Validated method parameters
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ProblemDetail> handleConstraintViolations(
            ConstraintViolationException exception, HttpServletRequest request) {
        
        Map<String, String> violations = new HashMap<>();
        for (ConstraintViolation<?> violation : exception.getConstraintViolations()) {
            violations.put(violation.getPropertyPath().toString(), violation.getMessage());
        }

        Map<String, Object> properties = new HashMap<>();
        properties.put("violations", violations);

        return buildProblem(
            HttpStatus.BAD_REQUEST,
            "Constraint Violation",
            "Parameter validation failed",
            ProblemTypes.CONSTRAINT_VIOLATION,
            request,
            properties,
            exception
        );
    }

    /* ------------ Legacy Exception Handlers (to be migrated) ------------- */
    
    // These will be removed once all exceptions extend BusinessException
    
    @ExceptionHandler(InvalidEmailException.class)
    public ResponseEntity<ProblemDetail> handleInvalidEmail(InvalidEmailException exception, HttpServletRequest request) {
        return buildProblem(
            HttpStatus.BAD_REQUEST,
            "Invalid Email",
            exception.getMessage(),
            ProblemTypes.INVALID_EMAIL,
            request,
            null,
            exception
        );
    }

    @ExceptionHandler(SellerAccountForTheCompanyNameAlreadyExistsException.class)
    public ResponseEntity<ProblemDetail> handleCompanyNameTaken(
            SellerAccountForTheCompanyNameAlreadyExistsException exception, HttpServletRequest request) {
        return buildProblem(
            HttpStatus.BAD_REQUEST,
            "Company Name Taken",
            exception.getMessage(),
            ProblemTypes.COMPANY_NAME_TAKEN,
            request,
            null,
            exception
        );
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleResourceNotFound(ResourceNotFoundException exception, HttpServletRequest request) {
        return buildProblem(
            HttpStatus.NOT_FOUND,
            "Resource Not Found",
            exception.getMessage(),
            ProblemTypes.NOT_FOUND,
            request,
            null,
            exception
        );
    }

    @ExceptionHandler(ProductOutOfStockException.class)
    public ResponseEntity<ProblemDetail> handleProductOutOfStock(ProductOutOfStockException exception, HttpServletRequest request) {
        return buildProblem(
            HttpStatus.NOT_FOUND,
            "Product Out of Stock",
            exception.getMessage(),
            ProblemTypes.PRODUCT_OUT_OF_STOCK,
            request,
            null,
            exception
        );
    }

    @ExceptionHandler(ProductNotInCartException.class)
    public ResponseEntity<ProblemDetail> handleProductNotInCart(ProductNotInCartException exception, HttpServletRequest request) {
        return buildProblem(
            HttpStatus.NOT_FOUND,
            "Product Not in Cart",
            exception.getMessage(),
            ProblemTypes.PRODUCT_NOT_IN_CART,
            request,
            null,
            exception
        );
    }

    @ExceptionHandler(SellerAlreadyExistsException.class)
    public ResponseEntity<ProblemDetail> handleSellerAlreadyExists(SellerAlreadyExistsException exception, HttpServletRequest request) {
        return buildProblem(
            HttpStatus.CONFLICT,
            "Seller Already Exists",
            exception.getMessage(),
            ProblemTypes.SELLER_ALREADY_EXISTS,
            request,
            null,
            exception
        );
    }

    @ExceptionHandler(ForbiddenOperationException.class)
    public ResponseEntity<ProblemDetail> handleForbiddenOperation(ForbiddenOperationException exception, HttpServletRequest request) {
        return buildProblem(
            HttpStatus.FORBIDDEN,
            "Forbidden Operation",
            exception.getMessage(),
            ProblemTypes.FORBIDDEN_OPERATION,
            request,
            null,
            exception
        );
    }

    @ExceptionHandler(DuplicateProductException.class)
    public ResponseEntity<ProblemDetail> handleDuplicateProduct(DuplicateProductException exception, HttpServletRequest request) {
        return buildProblem(
            HttpStatus.BAD_REQUEST,
            "Duplicate Product",
            exception.getMessage(),
            ProblemTypes.DUPLICATE_PRODUCT,
            request,
            null,
            exception
        );
    }

    /* ------------ Catch-All Exception Handler ------------- */
    
    /**
     * Handles any uncaught exceptions (500 Internal Server Error)
     * Never leaks internal details to the client
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleGenericException(Exception exception, HttpServletRequest request) {
        // Log the full exception for debugging but don't expose details
        log.error("Unhandled exception occurred", exception);
        
        return buildProblem(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "Internal Server Error",
            "An unexpected error occurred. Please try again later.",
            ProblemTypes.INTERNAL_ERROR,
            request,
            null,
            exception
        );
    }
}
