# Chapter 3 - Product Service

> Goal: Understand why Product Service exists, what it owns, and how to design its APIs.

## 1. Why Does Product Service Exist?

In e-commerce, before a customer can buy:
- Does the product exist?
- Is it active/available?
- What is the current price?
- Is enough stock available?

Instead of letting every module maintain product info, we create **Product Service** as the **single source of truth** for all product information.

---

## 2. Responsibilities of Product Service

### Product Service Owns:
- Product details (name, description, category)
- Product price
- Product availability status (active/inactive)
- Available stock/inventory
- Stock validation and calculations

### Product Service Does NOT Own:
- Creating orders
- Processing payments
- Sending notifications
- Maintaining customer information

Following the Single Responsibility Principle keeps each service focused on one business capability.

---

## 3. Database Ownership

Product Service owns the `products` table exclusively.

Example schema:
```
| id | name      | price | stock |
|----|-----------|-------|-------|
| 1  | iPhone 15 | 80000 | 20    |
```

No other service should directly update this table.

**Interview Q:** Can Order Service update stock directly?

**Answer:** No. Only Product Service owns product data. Other services must request through APIs.

---

## 4. APIs We Implemented

- Create Product (POST)
- Get Product By Id (GET)
- Get All Products (GET)
- Update Product (PUT)
- Delete Product (DELETE)
- Reduce Stock (POST /products/{id}/reduce-stock)

These represent business operations exposed by Product Service.

---

## 5. Why Reduce Stock is a Separate API?

### Bad Design:
```
PUT /products/{id}
Body: { "stock": 18 }
```

Problem: Order Service is deciding the final stock value.

### Good Design:
```
POST /products/{id}/reduce-stock
Body: { "quantityToReduce": 2 }
```

Advantages:
1. Product Service reads current stock
2. Validates availability
3. Calculates remaining stock
4. Updates database

Product Service maintains control of inventory business logic.

**Interview Q:** Why not send the new stock value?

**Answer:** Product Service should own inventory calculations. External services request a business operation, not raw state manipulation.

---

## 6. Validation

Validation enforces data correctness at the API boundary.

Examples:
- Product name cannot be blank
- Price must be positive
- Stock cannot be negative

Benefits:
- Fail fast (reject invalid requests immediately)
- Prevent invalid data from entering system
- Cleaner business logic layer

---

## 7. Exception Handling

Business exceptions specific to Product Service:

- `ProductNotFoundException` → 404
- `InvalidProductDataException` → 400
- `OutOfStockException` → 409 (Conflict - business state issue)

Global exception handling ensures consistent API responses.

---

## 8. Why Product Service is the Source of Truth

**Scenario:**
- Today: iPhone price = ₹80,000
- Customer purchases one
- Tomorrow: Price changes to ₹90,000

**Question:** Should yesterday's order show ₹80,000 or ₹90,000?

**Answer:** ₹80,000 (the price when purchased).

**Design consequence:**
- Current price lives in Product Service (changes over time)
- Historical price lives in Order Service (frozen at purchase time)

This separation preserves business correctness.

---

## 9. Common Mistakes

❌ Letting Order Service calculate stock  
❌ Sharing Product database with other services  
❌ Returning database entities directly from APIs  
❌ Mixing Order and Product business logic  
❌ Exposing internal exceptions to clients  

---

## 10. Best Practices

- Validate requests at controller boundary
- Keep business rules in Product Service
- Expose meaningful business operations via APIs
- Return consistent error responses
- Never allow another service to bypass Product APIs
- Use descriptive exception names
- Document API contracts clearly

---

## 11. Interview Questions

**Q1: Why create a Product Service?**

A: To centralize product-related business logic and provide a single source of truth for product information, preventing inconsistency.

**Q2: Why should Product Service own stock?**

A: Stock belongs to the product domain. Centralizing inventory logic prevents duplication and ensures consistent calculations.

**Q3: Why expose Reduce Stock API instead of Update Stock?**

A: Reduce Stock represents a business operation. It allows Product Service to validate, calculate, and update stock atomically.

**Q4: What data should Product Service expose?**

A: Only data required by consumers. Internal implementation details and database structure remain hidden.

**Q5: Can Product Service call Order Service?**

A: Technically yes, but avoid circular dependencies. Service interactions should follow business ownership patterns.

---

## Summary

- Product Service owns all product data
- Product Service owns stock calculations and validation
- Product Service is the source of truth
- Other services communicate through APIs only
- Business logic must live where it can be enforced

---

## Cheat Sheet

**Product Service owns:**
- ✓ Product CRUD
- ✓ Price
- ✓ Stock management
- ✓ Validation
- ✓ Reduce Stock operations
- ✓ Source of Truth

**Product Service does NOT own:**
- ✗ Orders
- ✗ Payments
- ✗ Notifications
