# Kafka Learning Notes (Before Coding)

> Goal: Understand why each Kafka concept exists before implementation.

## 1. Why Kafka?

Order Service should not directly depend on Email, Audit, Analytics, or Notification availability.

Pattern:

- Order Service creates order
- Order Service publishes one `OrderCreated` event
- Interested services consume independently

Result: better decoupling and failure isolation.

---

## 2. REST vs Event-Driven

### Use REST when response is required now

Example: Order Service -> Product Service for product validation and stock check.

### Use Kafka when announcing a business fact

Example: `OrderCreated` after successful order creation.

---

## 3. Producer

A producer publishes events to Kafka topics.

Example: Order Service publishes `OrderCreated`.

---

## 4. Broker

Kafka broker receives, stores, and serves events.

Benefits:

- Decouples producer and consumer lifecycles
- Stores data durably
- Enables retry and replay
- Supports multiple independent consumer groups

---

## 5. Topic

A topic is a logical stream/category of similar events.

Examples: `order-created`, `payment-completed`, `inventory-updated`.

---

## 6. Consumer

A consumer reads events from a topic.

Example: Email Service consumes `OrderCreated`.

---

## 7. Consumer Group

A consumer group lets multiple instances of one service share work.

Example: `email-service` group with Email-1, Email-2, Email-3.

Outcome:

- Better throughput
- No duplicate processing within the same group

---

## 8. Partitions

Partitions enable parallelism and scale.

Rule: one partition is actively processed by one consumer at a time per consumer group.

---

## 9. Message Key

Message key controls partition selection.

Example: key = `orderId`.

Then `OrderCreated`, `OrderUpdated`, `OrderCancelled` for the same order go to the same partition and keep order there.

---

## 10. Offset

Offset is the record position in a partition.

Consumer resume behavior:

- Last committed offset = 120
- After restart, continue from 121

---

## 11. Why Kafka Keeps Messages

Kafka does not remove a record just because one consumer read it.

Why:

- Other consumer groups may still need it
- Replay is possible for recovery/reprocessing
- New consumers can read old events

Retention is configured by time and/or size.

---

## 12. Replication

Each partition can have replicas across brokers.

If one broker fails, another replica can take over, improving availability.

---

## 13. Leader and Followers

- Producer writes to partition leader
- Followers replicate leader data
- On leader failure, an in-sync follower is promoted

---

## 14. ACKS

- `acks=0`: do not wait for broker acknowledgement (fast, lowest safety)
- `acks=1`: wait for leader acknowledgement
- `acks=all`: wait for leader + in-sync replicas (highest durability)

---

## 15. Ordering

Kafka guarantees ordering only within a single partition.

---

## 16. Maximum Parallelism

For one topic and one consumer group:

Maximum active consumers = number of partitions.

Example: 3 partitions + 5 consumers -> 3 active, 2 idle.

---

## 17. Async Thread vs Kafka

Async thread only:

- In-process memory
- Work can be lost if process crashes

Kafka-backed async:

- Durable storage
- Consumer recovery after restart
- Replay support

---

## 18. Interview Definition

Kafka is a distributed event streaming platform that decouples microservices by letting producers publish business events and consumers process them asynchronously. It provides durable storage, replay, partition-based parallelism, and fault tolerance through replication.
