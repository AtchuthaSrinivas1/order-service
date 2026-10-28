# Chapter 10 - API Design

> Goal: Design REST APIs that are clean, intuitive, and maintainable.

## Learning Objectives

- Understand good REST API design principles
- Distinguish between CRUD and business APIs
- Learn proper HTTP method usage
- Design APIs that express business intent

---

## 1. Why API Design Matters

An API is a **contract between services**.

Once other services (or external consumers) depend on your API, changing it becomes expensive and risky.

Good APIs are:
- **Simple** - easy to understand and use
- **Consistent** - predictable patterns
- **Predictable** - behavior matches expectations
- **Backward compatible** - changes don't break consumers

---

## 2. CRUD API vs Business API

### CRUD API (Generic, Data-Focused)
```
PUT /products/{id}
Body: { "stock": 18 }
```

**Problem:** Order Service decides final stock value. Product Service has no control.

### Business API (Intent-Focused)
```
POST /products/{id}/reduce-stock
Body: { "quantityToReduce": 2 }
```

**Benefit:** Expresses business intent. Product Service maintains business logic.

**Key insight:** APIs should express **what action is requested**, not expose raw data manipulation.

---

## 3. HTTP Methods

| Method | Usage | Example |
|--------|-------|---------|
| GET | Read/retrieve resource | GET /products/{id} |
| POST | Create new resource OR business action | POST /products or POST /products/{id}/reduce-stock |
| PUT | Replace entire resource | PUT /products/{id} |
| PATCH | Partial resource update | PATCH /products/{id} |
| DELETE | Remove resource | DELETE /products/{id} |

---

## 4. API Design in Our Project

### Product Service APIs

**Queries (GET):**
- `GET /products/{id}` - Get single product
- `GET /products` - List all products

**Commands (POST/PUT/DELETE):**
- `POST /products` - Create product
- `PUT /products/{id}` - Update product details
- `DELETE /products/{id}` - Delete product
- `POST /products/{id}/reduce-stock` - Business action: reduce inventory

### Order Service APIs

**Queries (GET):**
- `GET /orders/{id}` - Get order details
- `GET /orders` - List orders (with pagination)

**Commands (POST):**
- `POST /orders` - Create order

---

## 5. Best Practices

- **Use nouns for resources** (not verbs)
  - ✓ `POST /products` (resource: products)
  - ✗ `POST /createProduct` (verb in URL)

- **Keep URLs simple and hierarchical**
  - ✓ `POST /orders/{orderId}/items`
  - ✗ `POST /createOrderItem?orderId=123&itemId=456`

- **Return appropriate HTTP status codes**
  - 200 = Success
  - 201 = Created
  - 400 = Bad request
  - 404 = Not found
  - 409 = Conflict

- **Validate requests** at API boundary

- **Never expose database schema** in responses
  - Use DTOs (Data Transfer Objects)
  - Hide implementation details

- **Version APIs if breaking changes needed**
  - `GET /v1/products`
  - `GET /v2/products`

---

## 6. API Versioning

When business changes require breaking API changes, version:

```
GET /api/v1/products/{id}   // Old version
GET /api/v2/products/{id}   // New version (different response)
```

**Rule:** Only version when completely necessary. Backward compatibility is better.

---

## 7. Interview Questions

**Q: Why POST for reduce-stock instead of PUT?**

A: Because reducing stock is a **business operation**, not replacing a resource. PUT is for replacing entire resource state. POST is for business actions.

**Q: PUT vs PATCH?**

A: PUT replaces the entire resource (sends all fields). PATCH updates only selected fields (sends only changed fields).

**Q: Should API URLs contain verbs?**

A: Generally no. Use nouns (resources) and let HTTP method convey action. Exception: complex business operations like `/reduce-stock`.

**Q: How do you handle API versioning?**

A: Prefer backward-compatible changes. If breaking changes required, version in URL path (`/v2/`) or header.

---

## 8. REST Constraints (Reminder)

REST APIs should be:
- **Stateless** - each request contains all context needed
- **Cacheable** - responses can be cached when appropriate
- **Uniform Interface** - consistent patterns across APIs
- **Client-Server** - clear separation between client and server

---

## Summary

- Design APIs to express business intent, not just data manipulation
- Use HTTP methods correctly (GET for read, POST for create/action, PUT for replace)
- URLs should be resource-focused with nouns, not verb-focused
- Return appropriate status codes
- Never expose database schema directly
- Maintain backward compatibility
- Use DTOs to decouple database from API

---

## Cheat Sheet

**HTTP Methods:**
- GET → Read
- POST → Create / Business Action
- PUT → Replace entire resource
- PATCH → Update specific fields
- DELETE → Remove

**Design Patterns:**
- Resource nouns: `/products`, `/orders`
- Business actions: `POST /products/{id}/reduce-stock`
- Hierarchical: `POST /orders/{orderId}/items`

**Status Codes:**
- 200 = OK
- 201 = Created
- 400 = Bad Request
- 404 = Not Found
- 409 = Conflict
- 500 = Server Error
