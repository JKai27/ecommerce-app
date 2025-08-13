# RFC 7807 Problem Details Migration Guide

## 🎯 What is RFC 7807 and Why Use It?

**RFC 7807** defines a standard JSON format for HTTP error responses called "Problem Details". Instead of different APIs returning errors in different formats, everyone uses the same structure.

### Before (Inconsistent):
```json
// Sometimes you get this
"Invalid email"

// Sometimes this  
{"error": "Not found", "timestamp": "2024-01-01T10:00:00Z"}

// Sometimes this
{"success": false, "message": "User exists", "data": null}
```

### After (Consistent RFC 7807):
```json
{
  "type": "https://api.shopeazy.com/problems/invalid-email",
  "title": "Bad Request", 
  "status": 400,
  "detail": "The email format is invalid",
  "instance": "/api/users",
  "timestamp": "2024-01-01T10:00:00Z",
  "correlationId": "abc-123-def",
  "email": "invalid-email-value"
}
```

## 🏗️ Migration Architecture

### Key Components:

1. **BusinessException** - Base class for all custom exceptions
2. **ProblemTypes** - Constants for stable error type URIs  
3. **GlobalExceptionHandler** - Converts exceptions to Problem Details
4. **Domain Exceptions** - Your specific business errors

## 📚 Learning Objectives

After this migration, you'll understand:

✅ How to create consistent error responses across your entire API  
✅ How to use Spring Boot 3's built-in `ProblemDetail` class  
✅ How to implement proper error logging with correlation IDs  
✅ How to handle validation errors professionally  
✅ How to make your API more maintainable and user-friendly  

## 🔧 Implementation Steps

### Step 1: Define Problem Types (Error Categories)
### Step 2: Update BusinessException (Already Done!)
### Step 3: Complete GlobalExceptionHandler Migration  
### Step 4: Migrate Domain Exceptions
### Step 5: Add Validation Message Support
### Step 6: Test Everything

---

## 🎯 Key Takeaways for Future Projects

### 1. **Consistency is King**
Always return the same error format. Frontend developers will thank you!

### 2. **Stable URIs Matter** 
Use URLs like `https://api.yourcompany.com/problems/validation-error` that don't change. These help:
- Developers identify error types programmatically
- Documentation and troubleshooting
- API versioning and evolution

### 3. **Correlation IDs Save Lives**
Every request gets a unique ID. When something breaks in production:
- Frontend sends correlation ID to support
- Support can find exact request in logs
- Much faster debugging!

### 4. **Security Through Obscurity Doesn't Work**
- 4xx errors (client mistakes): Show detailed messages
- 5xx errors (server mistakes): Hide internal details, log everything

### 5. **Validation Errors Need Structure**
Instead of: `"name is required, email is invalid"`  
Use: `{"name": ["is required"], "email": ["must be valid format"]}`

---

## 🚀 What We're Building

The migration will give you:

1. **Professional Error Responses** - Like big tech companies
2. **Better Debugging** - Correlation IDs in every response  
3. **Frontend-Friendly** - Structured validation errors
4. **Maintainable Code** - Centralized error handling
5. **Industry Standard** - RFC 7807 compliance

Let's get started! 🎉