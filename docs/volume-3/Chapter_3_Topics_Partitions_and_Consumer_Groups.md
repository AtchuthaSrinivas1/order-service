# Chapter 3 - Topics, Partitions, and Consumer Groups

## Why this chapter?

These three concepts define how Kafka scales and distributes work.

## Topic

A topic is a named stream of related events.

Examples in e-commerce:

- `order-created`
- `order-cancelled`
- `payment-completed`

## Partition

A topic is split into partitions to scale reads/writes.

Key points:

- Each partition is an ordered log
- Ordering is guaranteed only within that partition
- More partitions usually means more parallelism

## Consumer Group

Consumers in the same group share partitions.

Rules:

- One partition -> one active consumer in that group
- Different groups can read the same topic independently

Example:

- Topic has 4 partitions
- Group `email-service` has 2 instances -> both active
- Group `audit-service` has 4 instances -> all active

## Maximum Parallelism

For one topic in one group, max active consumers = partition count.

If consumers > partitions, extra consumers stay idle until rebalancing changes assignments.

## Summary

Topics organize event types, partitions enable scale, and consumer groups control parallel processing behavior.
