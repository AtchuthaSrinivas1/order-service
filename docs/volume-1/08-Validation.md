# Chapter 8 - Validation

> Goal: Understand when and where to validate, and the importance of fail-fast principle.

## Why Validation?

Validation ensures **invalid requests never enter business logic**, preventing bugs and maintaining data integrity.

---

## 1. API Boundary Validation

Validate requests **at the controller level** using Bean Validation annotations.

**Annotations:**
- `@NotNull` - field must not be null
- `@NotBlank` - string must have content
- `@Positive` - number must be > 0
- `@Min`, `@Max` - numeric ranges
- `@Size` - string/collection length

**Benefits:**
- **Fail fast** - reject bad requests before business logic executes
- **Cleaner service layer** - no validation code in business logic
- **Consistent API responses** - validation errors formatted consistently

---

## 2. Validation Rules in Our Project

### Product Service Validation
- Product name cannot be blank
- Price must be positive number
- Stock cannot be negative
- Category cannot be empty

### Order Service Validation
- ProductId must be positive
- Quantity must be greater than zero

---

## 3. Why Not Validate in Service Layer?

**Separation of concerns:**

| Layer | Responsibility |
|-------|-----------------|
| Controller | Input validation (format, type, range) |
| Service | Business validation (business rules) |

**Example:**

**Controller (request format):**
```java
@PostMapping
public OrderResponse createOrder(
    @Positive Long productId,     // controller validates format
    @Positive Integer quantity     // controller validates format
) { ... }
```

**Service (business rule):**
```java
// Service validates business rule
if (stock < quantity) {
    throw OutOfStockException(...);
}
```

---

## 4. Validation vs Business Validation

| Type | Example | Layer | Status |
|------|---------|-------|--------|
| **Input Validation** | quantity > 0 | Controller | Reject with 400 |
| **Business Validation** | stock availability | Service | Reject with 409 |

---

## 5. Global Validation Error Handling

Use `@ControllerAdvice` to centralize validation error responses:

```java
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorResponse handleValidationException(...) {
        // Consistent error format for all validation failures
    }
}
```

**Result:** All validation errors return same format.

---

## 6. Interview Questions

**Q: Why use Bean Validation?**

A: To validate requests declaratively with minimal boilerplate, reject invalid data early, and fail fast before business logic executes.

**Q: What's the difference between validation and business validation?**

A: Validation checks request format/structure (controller level). Business validation checks domain rules (service level) like stock availability.

**Q: Should business logic validate inputs?**

A: No. Controller should validate format, type, range. Service assumes valid input and focuses on business rules.

**Q: What if validation fails?**

A: Controller returns 400 Bad Request with error details before service layer is invoked.

---

## Summary

- Validate at controller boundary (request format)
- Service layer validates business rules
- Use Bean Validation annotations for declarative validation
- Global exception handler ensures consistent error responses
- Fail fast principle: reject invalid requests immediately

---

## Cheat Sheet

**Validation layer:**
```
Request
  ↓
@Validated annotations (fail if invalid)
  ↓
Controller (reject with 400 if format wrong)
  ↓
Service (validate business rules, reject with 409 if business rule fails)
```

**Examples:**
- `@NotBlank` String → 400 Bad Request
- `@Positive` Long → 400 Bad Request
- Stock < Quantity → 409 Conflict
