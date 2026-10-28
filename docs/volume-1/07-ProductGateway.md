# Chapter 7 - ProductGateway

> Goal: Understand why a gateway abstraction layer is essential for clean architecture.

## Learning Objectives

- Why a gateway layer is necessary
- Exception translation from technical to business
- Where resilience patterns (retry, circuit breaker) belong
- Separation of concerns

---

## 1. The Problem

Initially, Order Service called the Feign client directly.

**Problems:**
- Business logic knew HTTP client details (tight coupling)
- FeignException leaked into business code
- Infrastructure concerns mixed with business logic
- Retry and timeout scattered across OrderService
- Hard to test (Feign HTTP client is hard to mock)

---

## 2. Solution - ProductGateway

Introduce an abstraction layer (Gateway pattern):

```
OrderService (business logic)
  ↓
ProductGateway (abstraction/facade)
  ↓
ProductFeignClient (Feign HTTP client)
  ↓
HTTP
  ↓
Product Service
```

Gateway becomes the **single entry point** for all Product Service communication.

---

## 3. Responsibilities of ProductGateway

- **Call Product Service** via Feign
- **Translate technical exceptions** (FeignException) into business exceptions (ProductNotFoundException)
- **Hide Feign implementation** from OrderService
- **Future home** for retry logic, circuit breaker, and fallback handlers (Phase 2)

---

## 4. Exception Translation

Gateway translates infrastructure exceptions into domain-specific exceptions.

**Example Translations:**

| Feign Exception | → | Business Exception | HTTP Status |
|-----------------|---|------------------|-------------|
| FeignException.NotFound | → | ProductNotFoundException | 404 |
| FeignException.Conflict | → | OutOfStockException | 409 |
| FeignException.ServiceUnavailable | → | ProductServiceUnavailableException | 503 |
| FeignException.BadRequest | → | InvalidProductDataException | 400 |

**Key principle:** Business layer should only understand business concepts, not HTTP details.

---

## 5. Benefits

**Separation of Concerns:**
- Business logic (OrderService) doesn't know about HTTP
- Gateway knows about HTTP and resilience
- Feign knows about HTTP client mechanics

**Cleaner Code:**
- OrderService focuses on order business logic
- Gateway handles all Product Service interaction
- Tests can mock Gateway interface easily

**Flexibility:**
- Easy to replace Feign with gRPC later
- Easy to add circuit breaker in future
- Easy to change retry strategy

**Testability:**
- OrderService tests don't need Feign or HTTP setup
- Mock ProductGateway interface instead

---

## 6. Design Pattern: Facade

ProductGateway implements the **Facade** pattern:
- Complex HTTP client (Feign) hidden behind simple interface
- OrderService sees clean business API
- Changes to HTTP details don't affect OrderService

---

## 7. Interview Questions

**Q: Why create ProductGateway?**

A: To isolate infrastructure concerns from business logic, centralize downstream communication, and make code easier to test and maintain.

**Q: Can OrderService call Feign directly?**

A: Technically yes, but it violates separation of concerns and tightly couples business logic with HTTP client implementation details. Future changes to resilience patterns would require modifying OrderService.

**Q: What's the difference between a Gateway and a Service?**

A: A Service owns business logic and data. A Gateway is a thin abstraction layer that orchestrates calls to other services and handles infrastructure concerns.

**Q: Where should retry logic go?**

A: In the Gateway. The Gateway handles all resilience patterns. OrderService never knows retries happened.

---

## Summary

- Gateway abstracts infrastructure complexity from business logic
- Exception translation converts technical errors to business errors
- Gateway is thin—it orchestrates Feign calls, not business logic
- Single responsibility: ProductGateway only talks to Product Service
- Phase 2 will add retry, circuit breaker, and other resilience patterns to this layer

---

## Cheat Sheet

**Architecture:**
```
OrderService (knows about orders)
  ↓
ProductGateway (knows about HTTP and Product Service)
  ↓
Feign Client (knows about HTTP mechanics)
```

**Benefits:**
- ✓ Separation of concerns
- ✓ Easy testing
- ✓ Infrastructure isolated
- ✓ Business logic clean
- ✓ Future flexibility
