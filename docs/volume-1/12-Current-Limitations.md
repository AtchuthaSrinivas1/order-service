# Chapter 12 - Current Limitations

> Goal: Identify what doesn't work in Phase 1 architecture and why Phases 2-3 are needed.

## Learning Objectives

- Identify architectural limitations
- Understand why resilience patterns are necessary
- Learn about distributed transaction problem
- Understand the roadmap forward

---

## 1. Current Architecture

```
Client Request
  ↓
Order Service validates request
  ↓
Calls Product Service (REST)
  ↓
Product Service reduces stock
  ↓
Order Service saves order
  ↓
Response to client
```

This is **Phase 1**: Basic synchronous architecture.

**Works for:** Small scale, high availability of Product Service, fast networks.

**Breaks when:** Scale grows, services become unreliable, latency increases.

---

## 2. Limitation 1 - Tight Runtime Dependency

### The Problem

If Product Service is **unavailable**, Order creation **fails**.

```
If Product Service is down:
  → Order Service cannot create orders
  → Customers cannot place orders
  → Business blocked
```

### Why It's Bad

- No graceful degradation
- Entire application unavailable
- Circular failure (one failure cascades)

### Phase 2 Solution

Add resilience patterns:
- **Timeout** - stop waiting after X seconds
- **Retry** - retry failed requests
- **Circuit Breaker** - stop calling failing service
- **Fallback** - provide default response when service unavailable

---

## 3. Limitation 2 - Timeout Risk

### The Problem

If Product Service is **slow**, Order API is **slow**.

```
Product Service response time: 10 seconds
  → Order API response time: at least 10 seconds
  → Customers wait 10+ seconds to place order
  → Poor user experience
```

### Why It's Bad

- Slow APIs drive customers away
- Thread pools exhaust (servers can't handle load)
- Resource starvation (all threads blocked waiting)

### Phase 2 Solution

- **Timeout** - fail fast instead of waiting forever
- **Bulkhead** - limit concurrent calls to Product Service
- **Rate Limiting** - throttle inbound traffic

---

## 4. Limitation 3 - Distributed Transaction Problem

### The Problem (Critical)

```
Stock reduced: ✓ SUCCESS
  ↓
Save Order: ✗ FAILS
```

Result:
- Stock database: reduced ✓
- Order database: nothing created ✗
- **System is inconsistent**

Customer sees: no order, but inventory was decreased.

### Why It's Critical

- Data corruption
- Lost money
- Customer complaints
- Refund complexity

### Why @Transactional Doesn't Help

`@Transactional` only works **within one database**:

```java
@Transactional
public void createOrder() {
    // Can rollback database changes in Order Service
    orderRepository.save(order);  // Can rollback
}
```

**But across services:**

```java
// Cannot rollback across services
productService.reduceStock();    // Different database!
orderRepository.save(order);     // Can fail independently
```

### Phase 3 Solution

**Saga Pattern** - choreograph distributed transactions:

1. Order Service publishes `OrderCreated` event
2. Product Service consumes event, reduces stock
3. If stock reduction fails, compensating transaction: `CreateOrder` compensates with `CancelOrder`

Or **Two-Phase Commit** (less recommended in microservices).

---

## 5. Limitation 4 - No Fault Tolerance

Current application lacks:
- ✗ Retry logic (retries never happen)
- ✗ Circuit Breaker (keeps calling failing service)
- ✗ Fallback mechanism (no alternative path)
- ✗ Bulkhead isolation (all threads can block)
- ✗ Rate limiting (unlimited inbound traffic)

Result: Production outages.

### Phase 2 Solution

Implement Resilience4j patterns:
- Retry with exponential backoff
- Circuit breaker with state machine
- Fallback responses
- Bulkhead thread pools
- Rate limiting

---

## 6. Roadmap

### Phase 1 (Current) - Learning Foundation
- ✓ Basic microservices architecture
- ✓ Service boundaries
- ✓ REST communication
- ✓ Database per service
- ✓ Simple exception handling

### Phase 2 - Resilience and Reliability
- □ Timeout configuration
- □ Retry with exponential backoff
- □ Circuit breaker pattern
- □ Fallback/default responses
- □ Bulkhead isolation
- □ Rate limiting
- Tool: Resilience4j

### Phase 3 - Event-Driven Architecture
- □ Kafka message broker
- □ Event publishing (OrderCreated)
- □ Event consuming
- □ Saga pattern for distributed transactions
- □ Eventual consistency
- □ Decoupled services

---

## 7. Interview Questions

**Q: What is the biggest problem in your current implementation?**

A: Distributed consistency. Stock reduction and order creation are separate transactions across services. If stock succeeds but order fails, system becomes inconsistent.

**Q: Why is @Transactional not enough?**

A: @Transactional only works within one database. It cannot roll back changes in another microservice.

**Q: How would you solve the distributed transaction problem?**

A: Use Saga pattern (orchestrate compensating transactions) or implement two-phase commit (not recommended for microservices).

**Q: What happens if Product Service is down?**

A: Order creation fails. Customers cannot place orders. With Phase 2, we'd have circuit breaker to fail fast and fallback response.

**Q: Why not use RabbitMQ instead of Kafka?**

A: Both work. Kafka is better for event replay, high throughput, and multiple independent consumers. RabbitMQ is better for task queues.

---

## 8. When to Add Phases 2 and 3

**Add Phase 2 when:**
- Services start experiencing timeouts
- Need graceful degradation
- Expecting production traffic spikes
- Services have variable availability

**Add Phase 3 when:**
- Distributed consistency becomes critical
- Side effects must be reliable
- Need to decouple services further
- Need event replay/audit trail

---

## Summary

- Phase 1 works for learning and small scale
- Tight runtime dependency causes cascading failures
- Slow services cause thread pool exhaustion
- Distributed transactions are unsolvable with synchronous communication
- Phase 2 adds resilience (Resilience4j)
- Phase 3 adds asynchronous events (Kafka) and Saga pattern
- Production systems need all three phases

---

## Cheat Sheet

**Phase 1 (Current):**
- Synchronous REST
- No retry/circuit breaker
- No distributed transaction handling
- Fails if downstream unavailable

**Phase 2:**
- + Resilience4j patterns
- + Timeout, retry, circuit breaker
- + Still synchronous
- Better fault tolerance

**Phase 3:**
- + Kafka for async events
- + Saga pattern for distributed transactions
- + Event-driven architecture
- + Proper eventual consistency
