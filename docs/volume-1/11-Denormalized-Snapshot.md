# Chapter 11 - Denormalized Snapshot

> Goal: Understand when and why controlled duplication is appropriate in distributed systems.

## Learning Objectives

- Understand normalization vs denormalization
- Learn why Order stores a copy of product data
- Design data consistency strategies in microservices

---

## 1. What is Normalization?

In relational database design, **normalization** means:
- Store each data item **only once**
- Avoid duplication
- Use foreign keys to reference data

**Normalized Example:**

```
Orders table:
| id | productId | quantity |
|----|-----------|----------|
| 1  | 123       | 2        |

Products table:
| id  | name     | price  |
|-----|----------|--------|
| 123 | iPhone   | 80000  |
```

To show order details with product name, join the tables:
```sql
SELECT o.id, p.name, p.price, o.quantity 
FROM orders o 
JOIN products p ON o.productId = p.id
```

**Problem in microservices:** Cross-service joins are NOT allowed!

---

## 2. The Microservices Problem

**Constraint:** Product Service owns Products database. Order Service owns Orders database.

**Cannot do:** SQL join across services.

```sql
-- WRONG! Order Service cannot access Product Service database
SELECT o.id, p.name, p.price 
FROM order_service.orders o 
JOIN product_service.products p ON o.productId = p.id
```

**Solution:** Denormalization (controlled duplication).

---

## 3. Denormalization

**Denormalization** means: storing data in **multiple places** for fast access and independence.

**Denormalized Example:**

```
Orders table (in Order Service):
| id | productId | productName | productPrice | quantity |
|----|-----------|-------------|--------------|----------|
| 1  | 123       | iPhone      | 80000        | 2        |
```

Order Service stores a **copy** of product data at order creation time.

**Advantages:**
- No cross-service joins
- Order Service is independent
- Fast reads (all data in one table)
- Historical accuracy (stores price at purchase time)

---

## 4. Why Store Product Snapshot?

**Scenario:**

Today:
- iPhone price: ₹80,000
- Customer places order

Six months later:
- iPhone price: ₹60,000 (price drop)

**Question:** Should the old order show ₹80,000 or ₹60,000?

**Answer:** ₹80,000 (the price customer actually paid).

**Design consequence:**
- Order always shows historical data
- Product always shows current data
- Order is a snapshot (frozen in time)
- Product is live (changes over time)

---

## 5. What Order Stores

At order creation, Order Service stores **snapshot**:

```java
{
    "orderId": 1,
    "productId": 123,
    "productName": "iPhone 15",
    "productPrice": 80000,      // Price at purchase time
    "quantity": 2,
    "totalAmount": 160000,
    "orderDate": "2026-08-13"
}
```

This data is **frozen**—never changes even if product changes.

---

## 6. Is Duplication Bad?

**In traditional relational databases:** Yes, duplication is bad (violates normal forms).

**In microservices:** Controlled duplication is often the **better choice**.

**Why?**

| Concern | Normalization | Denormalization |
|---------|---------------|-----------------|
| Data consistency | Single source | Multiple copies |
| Query performance | Requires joins | Fast reads |
| Service independence | Tight coupling | Loose coupling |
| Historical accuracy | Poor | Excellent |

---

## 7. Consistency Guarantees

By storing snapshot, Order Service accepts **eventual consistency**:

**Immediate consistency:** Product and Order show same price instantly.

**Eventual consistency:** Order shows historical price; Product shows current price.

**Trade-off:** Loose coupling worth the eventual consistency tradeoff.

---

## 8. Interview Questions

**Q: Why duplicate data?**

A: Controlled duplication in microservices preserves historical accuracy, enables service independence, and avoids tight coupling through shared databases.

**Q: Isn't duplication bad?**

A: In relational databases, yes (violates normal forms). In distributed systems, it's often the right trade-off for independence and consistency.

**Q: How do you keep snapshots up to date?**

A: You don't. Snapshots are **historical records**. They reflect data at that moment and never change.

**Q: What if product details change after order?**

A: Order snapshot remains unchanged. Product shows new data. No inconsistency—they serve different purposes.

**Q: How do you handle price changes in snapshots?**

A: You don't. Order price is frozen at purchase time. If customer disputes, you have historical record.

---

## 9. When to Use Snapshots

Use snapshots when:
- ✓ Data is historical (orders, invoices, receipts)
- ✓ Services are independent
- ✓ Accuracy requires point-in-time data
- ✓ Joining across services is not acceptable

Don't use snapshots for:
- ✗ Real-time current state (stock, inventory)
- ✗ Small datasets in same service
- ✗ When strong consistency is required

---

## Summary

- Normalization works in single database systems
- Microservices cannot do cross-service joins
- Denormalization (snapshots) is controlled duplication
- Snapshots preserve historical accuracy
- Snapshots enable service independence
- Order Service snapshots never change
- Product Service data changes over time
- This is acceptable eventual consistency

---

## Cheat Sheet

**Current Data (Product Service):**
- ✓ Single source of truth
- ✓ Always up-to-date
- ✗ Can change at any time

**Historical Data (Order Service Snapshot):**
- ✓ Frozen in time
- ✓ Historically accurate
- ✗ Doesn't change when product changes

**Pattern:**
```
At Order Creation:
  Product Service provides current data
  → Order Service stores copy
  → Order Service snapshot never changes again
```
