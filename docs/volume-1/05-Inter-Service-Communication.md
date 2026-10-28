# Chapter 5 - Inter-Service Communication

> Goal: Understand how and why microservices communicate, and compare synchronous vs asynchronous approaches.

## Learning Objectives

- Why microservices must communicate
- Synchronous vs Asynchronous communication patterns
- Why REST is chosen for Phase 1
- Limitations of current approach

---

## 1. The Communication Problem

In a monolith, modules call each other using direct Java method calls:

```
OrderService.createOrder() → ProductService.validateProduct()
```

Both run in the same JVM process, same memory space.

**In microservices:**
Order Service and Product Service are separate applications running on different ports/servers.

**Result:** Direct method calls are impossible. They must communicate over the network using protocols like HTTP, gRPC, or message queues.

---

## 2. Why Communication is Required in Our Project

Order Service receives from client:
- productId
- quantity

Before creating an order, Order Service must know:
- Does the product exist?
- What is the latest price?
- Is enough stock available?

**Only Product Service owns this information.**

Therefore Order Service must communicate with Product Service via API.

---

## 3. Types of Communication

### Synchronous Communication

**Pattern:** Caller waits for response before continuing.

**Examples:**
- REST/HTTP
- gRPC

**Advantages:**
- Immediate response (caller knows result right away)
- Easy to understand and debug
- Simple error handling

**Disadvantages:**
- Tight runtime dependency (if callee is down, caller fails)
- Timeout risk (network delays block caller)
- Cascading failures (one slow service slows down entire chain)

### Asynchronous Communication

**Pattern:** Producer publishes event and continues; consumer processes later.

**Examples:**
- Kafka
- RabbitMQ
- SNS/SQS

**Advantages:**
- Loose coupling (services are independent)
- Better resilience (failures are isolated)
- Decouples service lifecycles

**Disadvantages:**
- Eventual consistency (data not instantly synchronized)
- More complex debugging (harder to trace flow)
- Complex distributed transaction handling

**Current project:** Uses **synchronous REST** (Phase 1).

---

## 4. Why REST for Phase 1?

REST is ideal because:
- **Simple:** Widely understood HTTP
- **Language independent:** Works across tech stacks
- **Widely supported:** Native Spring support
- **Easy to debug:** Can test with curl, Postman

**Key requirement:** Order creation cannot proceed until product validation completes.

Synchronous blocking is appropriate here.

---

## 5. Communication Flow

```
Client request
  ↓
Order Controller
  ↓
Order Service (business logic)
  ↓
ProductGateway (abstraction layer)
  ↓
Feign Client (HTTP client)
  ↓
HTTP Request
  ↓
Product Service
  ↓
HTTP Response
```

Each layer has a specific responsibility.

---

## 6. Current Limitations

### If Product Service is slow:
- Order API becomes slow
- Customers wait longer
- System throughput decreases

### If Product Service is unavailable:
- Order creation fails
- Customers cannot place orders
- Business is impacted

**Solutions:**
- Phase 2: Resilience4j (timeout, retry, circuit breaker, fallback)
- Phase 3: Kafka (asynchronous, event-driven)

---

## 7. Interview Questions

**Q: Why does Order Service communicate with Product Service?**

A: Product Service is the source of truth for product information (price, stock, availability). Order Service must fetch current data.

**Q: Why not communicate through the database?**

A: It breaks service boundaries, creates tight coupling through schema dependencies, bypasses business rules, and prevents independent evolution.

**Q: When should you use synchronous vs asynchronous?**

A: Use synchronous when immediate response is required (order validation). Use asynchronous for notifications and non-critical side effects.

**Q: What are the risks of synchronous communication?**

A: Tight coupling, cascading failures, timeout risks, and dependency on downstream service availability.

---

## Summary

- Microservices must communicate over the network
- Synchronous communication (REST) is used when immediate response is needed
- Asynchronous communication (Kafka) is used for event-driven, decoupled flows
- Current Phase 1 uses synchronous for order validation
- Future phases will add resilience and async patterns

---

## Cheat Sheet

**Monolith:**
- Method calls
- In-process
- Synchronous

**Microservices Phase 1 (Current):**
- REST/HTTP calls
- Network communication
- Synchronous

**Microservices Phase 2:**
- + Resilience4j (timeout, retry, circuit breaker)

**Microservices Phase 3:**
- + Kafka (asynchronous events)
- Event-driven architecture
