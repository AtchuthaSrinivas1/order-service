# Chapter 9 - Exception Handling

> Goal: Centralize exception handling and return consistent, meaningful error responses.

## Learning Objectives

- Why global exception handling is necessary
- How to map exceptions to HTTP status codes
- Exception translation principle
- Consistent error response format

---

## 1. The Problem

Without centralized handling, exceptions are handled inconsistently across controllers:

```java
// Controller 1
try {
    service.create();
} catch (ProductNotFoundException e) {
    return ResponseEntity.status(404).body(...);
}

// Controller 2
try {
    service.create();
} catch (ProductNotFoundException e) {
    return ResponseEntity.status(400).body(...);  // Different status!
}
```

Result: Inconsistent, unpredictable API responses.

---

## 2. Global Exception Handling Solution

Use `@ControllerAdvice` to centralize exception handling:

```java
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ProductNotFoundException.class)
    public ErrorResponse handle(ProductNotFoundException e) {
        // Consistent handling for ALL ProductNotFoundException
    }
}
```

**Benefits:**
- Cleaner controllers (no try-catch boilerplate)
- Standard error format across entire API
- Easier maintenance (change once, applies everywhere)
- Consistent error documentation

---

## 3. Exception Types in Our Project

### Business Exceptions
- `ProductNotFoundException` → 404 (Product not found)
- `InvalidProductDataException` → 400 (Validation failed)
- `OutOfStockException` → 409 (Conflict - inventory issue)

### Technical Exceptions
- `ProductServiceUnavailableException` → 503 (Service down)

### Unexpected Errors
- `RuntimeException` → 500 (Internal server error)

---

## 4. HTTP Status Code Mapping

| Status | Exception | Meaning |
|--------|-----------|---------|
| 400 | InvalidProductDataException | Bad request (validation failure) |
| 404 | ProductNotFoundException | Resource doesn't exist |
| 409 | OutOfStockException | Conflict (business state issue) |
| 503 | ProductServiceUnavailableException | Downstream service unavailable |
| 500 | RuntimeException | Unexpected error |

---

## 5. Exception Translation Pattern

Gateway translates **technical exceptions** to **business exceptions**:

```
FeignException (technical)
  ↓
ProductGateway (translation)
  ↓
ProductNotFoundException (business)
  ↓
GlobalExceptionHandler (response)
  ↓
HTTP Response (404)
```

**Why?** OrderService should only deal with business concepts, not HTTP details.

---

## 6. Standard Error Response Format

Create a consistent error response structure:

```json
{
  "timestamp": "2026-08-13T16:40:45Z",
  "status": 404,
  "error": "Not Found",
  "message": "Product with ID 123 not found",
  "path": "/orders"
}
```

All errors use the same format, making API predictable.

---

## 7. Interview Questions

**Q: Why use @ControllerAdvice?**

A: To centralize exception handling, return consistent error responses across all endpoints, and reduce boilerplate in controllers.

**Q: Why 409 for insufficient stock?**

A: Status 409 (Conflict) is used when the request is valid but conflicts with the current server state (inventory level).

**Q: Why translate Feign exceptions?**

A: Business logic should depend on business concepts. Feign and HTTP are implementation details. Translating hides these details.

**Q: Should you log exceptions?**

A: Yes. Log full stack trace at ERROR level for debugging, but return sanitized message to client (don't expose internals).

**Q: Can global exception handler handle all exceptions?**

A: Most exceptions, yes. Business exceptions and technical exceptions should be handled globally. Only rarely should specific methods handle exceptions locally.

---

## 8. Best Practices

- Translate technical exceptions to business exceptions in Gateway
- Log full details for debugging
- Return minimal info to clients (security)
- Use appropriate HTTP status codes
- Return consistent error format
- Never expose stack traces to clients
- Business layer throws business exceptions
- Controller just handles requests/responses

---

## Summary

- Global exception handling centralizes error responses
- Exception translation hides infrastructure details
- Consistent error format improves API usability
- HTTP status codes should semantically match error type
- Business logic throws domain exceptions
- Gateway translates technical to domain exceptions

---

## Cheat Sheet

**Exception Flow:**

```
FeignException (HTTP detail)
  ↓ (translated in Gateway)
ProductNotFoundException (business concept)
  ↓ (handled globally)
GlobalExceptionHandler
  ↓ (mapped to status)
404 Not Found
```

**Status Codes:**
- 400 = Invalid input
- 404 = Resource not found
- 409 = Business state conflict
- 503 = Dependency unavailable
- 500 = Unexpected error
