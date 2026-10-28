# Kafka Concepts for Order Service

This file is a quick revision guide for the core Kafka topics used in your e-commerce learning flow.

## 1. What is Kafka?

Kafka is a distributed event streaming platform.  
It helps decouple microservices by allowing one service to publish events and many services to consume them asynchronously.

Example:

- Order Service publishes `OrderCreated`
- Email, Audit, Analytics, and Notification services consume it independently

## 2. REST vs Kafka

- Use **REST** when the caller needs an immediate response (example: Order Service validating product/stock with Product Service).
- Use **Kafka** when the service only needs to announce that an event happened (example: `OrderCreated` after successful order creation).

## 3. Kafka vs RabbitMQ (high level)

- RabbitMQ is strong for queue/task processing and complex routing patterns.
- Kafka is strong for durable event logs, replay, high throughput, and multiple independent consumers reading the same event stream.

## 4. Topic

A topic is a logical category for events.

Examples:

- `order-created`
- `payment-completed`
- `inventory-updated`

## 5. Partitions

Partitions split a topic for parallel processing and scaling.

Rule: within one consumer group, one partition is actively read by only one consumer at a time.

## 6. Message Key

The message key decides which partition stores a record.

Why key matters:

- Related events (same `orderId`) go to the same partition
- Order is preserved for that key within that partition

## 7. Consumer Groups

A consumer group is a set of consumers acting as one logical subscriber.

- Same group -> load balancing (no duplicate processing inside that group)
- Different groups -> each group gets its own copy of events

## 8. Offset

Offset is the position of a record inside a partition.

Consumer commits offsets to track progress and resume after restart.

## 9. Ordering Guarantee

Kafka guarantees ordering only **within a partition**, not across different partitions.

## 10. How to choose partition count?

Use these factors:

1. Expected throughput
2. Required consumer parallelism
3. Ordering needs (more partitions can reduce global ordering guarantees)
4. Near-future scale

Practical rule: partition count sets max active consumers per topic per consumer group.
