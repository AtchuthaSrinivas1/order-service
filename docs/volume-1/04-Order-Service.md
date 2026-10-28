# Chapter 4 - Order Service

> Goal: Understand Order Service responsibilities, inter-service communication, and data consistency challenges.

## 1. Why Does Order Service Exist?

From the customer's perspective, they simply want to place an order. Managing an order is fundamentally different from managing products.

Order Service manages the **complete order lifecycle** independently from product management.

---

## 2. Responsibilities of Order Service

### Order Service Owns:
- Creating orders
- Canceling orders
- Order history and retrieval
- Order status tracking
- Total amount calculation
- Customer purchase records

### Order Service Does NOT Own:
- Product information
- Product stock levels
- Product price updates
- Inventory calculations

Those belong exclusively to Product Service.

---

## 3. Order Creation Flow

Current synchronous implementation:

```
Client request
  ↓
Validate request data
  ↓
Call Product Service (via Feign)
  ↓
Validate product exists
  ↓
Validate stock availability
  ↓
Reduce stock
  ↓
Create order in Order Service database
  ↓
Save order
  ↓
Return response to client
```

This is **Synchronous Request-Response Communication** (REST/HTTP).

---

## 4. Why Call Product Service?

Order Service receives:
- productId
- quantity

But Order Service doesn't know:
- Product name
- Current price
- Available stock

**Therefore:** Order Service must call Product Service to fetch the latest product information.

Product Service remains the **source of truth** for product data.

---

## 5. Why Store Product Name and Price?

**Scenario:**
- Today: iPhone = ₹80,000 → Customer purchases one
- Tomorrow: iPhone price = ₹90,000

**Question:** Should yesterday's order display the old price (₹80,000) or new price (₹90,000)?

**Answer:** Old price (₹80,000). Orders are historical records.

**Design:**
Order Service stores a **Denormalized Snapshot**:
- productId
- productName
- price (at purchase time)
- quantity

This preserves historical accuracy and prevents inconsistency.

---

## 6. Calculating Total Amount

Total Amount = Price × Quantity

This value is calculated **at order creation time** and stored permanently.

**Why?**
Historical orders should reflect what the customer actually paid, regardless of future price changes.

---

## 7. Why Doesn't Order Service Calculate Stock?

**Before (Wrong):**
```
remainingStock = currentStock - quantity
Order Service sends remainingStock to Product Service
```

Result: Order Service decides Product business rules.

**After (Correct):**
```
Order Service sends: quantityToReduce = 2
Product Service:
  - Reads current stock
  - Validates availability
  - Calculates: remainingStock = currentStock - quantityToReduce
  - Updates database
```

Result: Product Service owns and enforces inventory rules (correct).

---

## 8. Current Limitation - Distributed Transaction Problem

**Current flow:**
```
Reduce Stock (succeeds)
  ↓
Save Order (fails)
```

**Problem:**
- Stock has been reduced
- But order was never created
- System is now inconsistent

**Result:**
- Customer sees inventory reduced
- Customer sees no order created
- Business data is corrupted

**Solution:** Use Kafka + Saga Pattern (Phase 3).

---

## 9. Best Practices

- Store a purchase snapshot (product name, price at purchase time)
- Never trust client-provided prices
- Always fetch current price from Product Service
- Keep Order Service focused on order management only
- Call other services exclusively through APIs
- Calculate and store totals at order creation time

---

## 10. Common Mistakes

❌ Accepting price from client (clients are untrusted)  
❌ Reading Product database directly  
❌ Calculating stock in Order Service  
❌ Updating Product database directly  
❌ Depending on another service's database schema  
❌ Not storing snapshot of product data  

---

## 11. Interview Questions

**Q1: Why does Order Service call Product Service?**

A: Because Product Service is the source of truth for product information (price, availability, stock).

**Q2: Why store product name and price in Order?**

A: Orders are historical business records. A snapshot preserves the exact purchase details even if the product changes later.

**Q3: Why shouldn't the client send product price?**

A: Clients are untrusted. They can manipulate request data. Price must always come from Product Service.

**Q4: Why calculate total amount during order creation?**

A: It preserves historical accuracy and avoids recalculating totals when prices change later.

**Q5: What is the biggest limitation of your current implementation?**

A: Synchronous communication with distributed transaction problem. If stock is reduced but order persistence fails, the system becomes inconsistent.

---

## Summary

- Order Service owns orders exclusively
- Product Service owns product data exclusively
- Orders must store a snapshot (copy of product data at purchase time)
- Always fetch current data from source services
- Calculate and store totals at creation time
- Current architecture has distributed consistency limitations to be solved in Phase 3

---

## Cheat Sheet

**Order Service owns:**
- ✓ Create Order
- ✓ Order Status
- ✓ Order History
- ✓ Snapshot Storage
- ✓ Total Amount

**Order Service depends on:**
- → Product Service (for validation and pricing)

**Future improvements:**
- → Resilience4j (Phase 2)
- → Kafka + Saga Pattern (Phase 3)
