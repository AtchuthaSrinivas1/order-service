# Chapter 2 - Service Boundaries (Bounded Context & Ownership)

> Goal: Understand what a microservice owns and the principle of service boundaries.

## 1. What are Service Boundaries?

A service boundary defines **what a microservice owns** and **what it should not own**.

Every microservice should own:
- A single business capability
- Its own business rules
- Its own database
- Its own APIs

A service boundary is determined by **business domain**, not by database tables.

---

## 2. Why Do We Need Service Boundaries?

Imagine an e-commerce system with modules: Product, Order, Payment, Inventory, Notification.

**Without boundaries:**
- Order Service directly updates Product table
- Payment Service updates Order table
- Result: tight coupling, schema dependencies, risky deployments

**With boundaries:**
- Each service owns its data exclusively
- Services communicate through APIs
- Changes are isolated and safe

---

## 3. Our Project Structure

### Product Service Owns:
- Product details
- Product price
- Product stock
- Product validation
- Stock calculations

### Product Service Does NOT Own:
- Orders
- Payments
- Customer information

### Order Service Owns:
- Order creation logic
- Order status
- Order history
- Total amount calculation
- Order snapshots

### Order Service Does NOT Own:
- Product stock
- Product price
- Product information updates

---

## 4. Service Ownership (Single Source of Truth)

Think of each service as an independent company:

**Product Service declares:**
"I own products. If you want product information, ask me."

**Order Service declares:**
"I own orders. Nobody should update my orders except through my APIs."

This is the principle of **Single Source of Truth** (SSOT).

---

## 5. Database Per Service Principle

### Correct Architecture:
```
Product Service ─→ products table
Order Service ─→ orders table
Inventory Service ─→ inventory table
```

### Wrong Pattern (Anti-pattern):
```
Order Service directly updates products table with: UPDATE products SET stock = ...
```

Why wrong?
- Bypasses Product Service's business rules
- Creates hidden dependencies
- Makes schema changes risky
- Breaks service autonomy

---

## 6. API Communication is Required

### Wrong Way:
Order Service reads Product database directly.

### Correct Way:
Order Service calls Product Service API to fetch product details.

Benefits:
- Encapsulation of data
- Loose coupling
- Business rule enforcement
- Clear contract between services

---

## 7. Business Logic Ownership

Business logic must live where it can be enforced.

**Example: Stock Calculation**

**Before (Wrong):**
```
Order Service calculates: newStock = currentStock - quantity
Order Service sends newStock to Product Service
Product Service receives newStock and updates database
```

Result: Order Service decides Product business rules (wrong).

**After (Correct):**
```
Order Service sends: quantityToReduce = 2
Product Service receives quantityToReduce
Product Service calculates: remainingStock = currentStock - quantityToReduce
Product Service validates and updates database
```

Result: Product Service owns inventory business logic (correct).

---

## 8. How to Identify a New Microservice

Ask these questions:

1. Is it a separate business capability?
2. Can another team own it independently?
3. Does it have different scaling requirements?
4. Does it have its own business rules?

If mostly yes → candidate for separate service.

---

## 9. Common Mistakes

❌ Shared database across services  
❌ One service updating another service's tables  
❌ Duplicate business logic across services  
❌ One service becoming responsible for everything  

---

## 10. Interview Questions

**Q: What is a service boundary?**

A: A service boundary defines the business capability, data ownership, and responsibilities of a microservice. Other services interact only through its public APIs.

**Q: Why shouldn't one service access another service's database?**

A: It breaks encapsulation, creates tight coupling through schema dependencies, bypasses business rules, and prevents independent evolution.

**Q: Why should Product Service own stock logic?**

A: Stock is part of the Product domain. Centralizing stock calculations ensures all business rules remain enforced and consistent.

**Q: Can data be duplicated across services?**

A: Yes. Controlled duplication (denormalization) is acceptable when it avoids cross-service joins and preserves historical data.

**Q: How do you decide service boundaries?**

A: Based on business capabilities, ownership, business rules, scaling requirements, and domain responsibilities—not database tables.

---

## Summary

- Business capability defines service boundaries
- Database belongs exclusively to the owning service
- APIs are the only communication channel
- Keep business logic inside the owning service
- Never share databases
- Never bypass another service's APIs

---

## Cheat Sheet

**Product Service:**
- Product CRUD
- Price
- Stock
- Source of Truth

**Order Service:**
- Orders
- Order Status
- Snapshot
- Total Amount

**Rule:** Own your data. Expose APIs. Do not expose your database.
