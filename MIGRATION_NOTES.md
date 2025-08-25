# RFC 7807 Problem Details Migration Notes

## 🎯 Migration Strategy Overview

This document outlines how to gradually migrate from your current mixed error response formats to RFC 7807 Problem Details standard.

## 📋 Migration Steps

### Phase 1: Foundation Setup ✅ COMPLETE
- [x] Create `ProblemTypes` constants with stable URIs
- [x] Update `BusinessException` base class 
- [x] Complete `GlobalExceptionHandler` with RFC 7807 support
- [x] Add correlation ID filter for request tracing
- [x] Create validation message properties (i18n)

### Phase 2: Domain Exception Migration (IN PROGRESS)
- [x] Update `InvalidEmailException` to extend `BusinessException`
- [ ] Migrate remaining exceptions to extend `BusinessException`:
  - [ ] `SellerAccountForTheCompanyNameAlreadyExistsException` 
  - [ ] `DuplicateProductException`
  - [ ] `ProductOutOfStockException`
  - [ ] `ProductNotInCartException`
  - [ ] `SellerAlreadyExistsException`
  - [ ] `ForbiddenOperationException`

### Phase 3: Legacy Handler Cleanup (PENDING)
Once all exceptions extend `BusinessException`, remove individual handlers from `GlobalExceptionHandler` (lines 144-251).

### Phase 4: Frontend Integration (PENDING)
- [ ] Update frontend error handling to use `parseProblem.ts` utility
- [ ] Replace old error parsing logic with RFC 7807 support
- [ ] Update error display components to handle structured validation errors

## 🔧 How to Migrate Each Exception

### Template for Exception Migration:

**Before:**
```java
public class YourException extends RuntimeException {
    public YourException(String message) {
        super(message);
    }
}
```

**After:**
```java
public class YourException extends BusinessException {
    public YourException(String message) {
        super(HttpStatus.BAD_REQUEST, ProblemTypes.YOUR_PROBLEM_TYPE, message);
    }
    
    // Add domain-specific constructor with properties
    public YourException(String message, String domainField) {
        super(HttpStatus.BAD_REQUEST, ProblemTypes.YOUR_PROBLEM_TYPE, message);
        withProperty("domainField", domainField);
    }
}
```

### Example: Migrate SellerAlreadyExistsException

1. **Update the exception class:**
```java
public class SellerAlreadyExistsException extends BusinessException {
    public SellerAlreadyExistsException(String message) {
        super(HttpStatus.CONFLICT, ProblemTypes.SELLER_ALREADY_EXISTS, message);
    }
    
    public SellerAlreadyExistsException(String message, String email) {
        super(HttpStatus.CONFLICT, ProblemTypes.SELLER_ALREADY_EXISTS, message);
        withProperty("email", email);
    }
}
```

2. **Update service usage:**
```java
// Old way:
throw new SellerAlreadyExistsException("Seller already exists");

// New way with context:
throw new SellerAlreadyExistsException("Seller already exists", user.getEmail());
```

3. **Remove individual handler** from `GlobalExceptionHandler` (handled by generic `BusinessException` handler)

## 🎨 API Versioning Strategy

### Option 1: Accept Header Negotiation
```java
@GetMapping(value = "/api/users/{id}", produces = {
    "application/problem+json",  // RFC 7807
    "application/json"           // Legacy
})
public ResponseEntity<?> getUser(@RequestHeader("Accept") String accept, ...) {
    // Return appropriate format based on Accept header
}
```

### Option 2: URL Versioning
```
/api/v1/users  -> Legacy format
/api/v2/users  -> RFC 7807 format
```

### Option 3: Gradual Migration (Recommended)
- All new endpoints use RFC 7807 from day one
- Existing endpoints gradually migrate
- Use feature flags for critical endpoints

## 🔍 Testing Strategy

### Test Categories:

1. **Unit Tests**: Test each exception type returns correct Problem Detail
2. **Integration Tests**: Test full request/response cycle 
3. **Contract Tests**: Ensure frontend can parse responses
4. **Manual Testing**: Use curl/Postman to verify responses

### Key Test Assertions:
```java
// Assert RFC 7807 compliance
assertThat(response.getContentType()).isEqualTo("application/problem+json");
assertThat(problemDetail.has("type")).isTrue();
assertThat(problemDetail.has("title")).isTrue(); 
assertThat(problemDetail.has("status")).isTrue();
assertThat(problemDetail.has("detail")).isTrue();
assertThat(problemDetail.has("instance")).isTrue();

// Assert custom fields
assertThat(problemDetail.has("timestamp")).isTrue();
assertThat(problemDetail.has("correlationId")).isTrue();
```

## 📊 Monitoring & Observability

### Key Metrics to Track:
- Error response times
- Problem type distribution 
- Correlation ID usage in support requests
- Frontend error handling success rates

### Logging Best Practices:
```java
// 4xx errors (client issues): WARN level
log.warn("[{}] Client error: {} -> {}", correlationId, request.getURI(), problem.getDetail());

// 5xx errors (server issues): ERROR level with stack trace  
log.error("[{}] Server error: {} -> {}", correlationId, request.getURI(), problem.getDetail(), exception);
```

## 🚀 Deployment Strategy

### Rolling Deployment:
1. Deploy new exception handling (backward compatible)
2. Monitor error rates and response formats
3. Gradually migrate domain exceptions
4. Update frontend error handling
5. Remove legacy error handlers

### Rollback Plan:
- Keep legacy handlers until frontend is fully updated
- Use feature flags to toggle RFC 7807 on/off
- Monitor correlation IDs for issues

## 📋 API Documentation Updates

### OpenAPI/Swagger Schema:
```yaml
responses:
  '400':
    description: Validation Error
    content:
      application/problem+json:
        schema:
          $ref: '#/components/schemas/ProblemDetail'
        example:
          type: "https://api.shopeazy.com/problems/validation-error"
          title: "Validation Failed"
          status: 400
          detail: "Request validation failed on 2 field(s)"
          errors:
            email: ["Invalid email format"]
            password: ["Password too short"]
```

### Documentation Updates:
- Document all problem type URIs
- Provide examples for each error scenario  
- Explain correlation ID usage for support
- Update error handling guides

## 🎯 Success Criteria

### Technical Goals:
- [ ] All exceptions return RFC 7807 compliant responses
- [ ] Response time impact < 10ms
- [ ] Zero breaking changes for existing clients
- [ ] 100% test coverage for error scenarios

### Business Goals:
- [ ] Faster issue resolution with correlation IDs
- [ ] Reduced support tickets from unclear errors
- [ ] Better developer experience for API consumers
- [ ] Consistent error handling across all endpoints

## 🔄 Maintenance Plan

### Regular Tasks:
- Review and update problem type URIs
- Monitor error rate trends
- Update validation messages based on user feedback
- Maintain correlation ID retention policies

### Quarterly Reviews:
- Analyze most common error types
- Update error messages for clarity
- Review and optimize error response performance
- Update documentation based on usage patterns

## 🎉 Completion Checklist

When migration is complete, you should have:
- [ ] All errors return `application/problem+json`
- [ ] Stable type URIs documented
- [ ] Correlation IDs in all responses
- [ ] Validation errors properly structured  
- [ ] No sensitive information in error responses
- [ ] Frontend successfully parsing Problem Details
- [ ] Updated API documentation
- [ ] Comprehensive test coverage

---

## 📚 Additional Resources

- [RFC 7807 Specification](https://tools.ietf.org/html/rfc7807)
- [Spring Boot Problem Details](https://spring.io/blog/2022/12/15/problem-details-for-http-apis-a-spring-boot-3-deep-dive)
- [Frontend Integration Guide](./parseProblem.ts)
- [Test Examples](./ExampleProblemDetailTest.java)