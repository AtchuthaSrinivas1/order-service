# Chapter 5 - Replication, Leaders, ACKs, and Ordering

## Replication

Kafka can replicate each partition to multiple brokers.

Benefits:

- Higher availability
- Better fault tolerance

## Leader and Followers

For each partition:

- One broker is leader
- Other replicas are followers

Producer/consumer communication is with the leader. Followers replicate from leader.

If leader fails, an eligible follower is promoted.

## ACKS (producer durability setting)

- `acks=0`: producer does not wait for broker response (lowest durability)
- `acks=1`: wait for leader acknowledgement
- `acks=all`: wait for acknowledgement from leader and in-sync replicas (highest durability)

## Ordering

Kafka ordering guarantee is partition-scoped:

- Guaranteed within one partition
- Not guaranteed across partitions

For strict per-order sequencing in e-commerce, publish with key = `orderId`.

## Summary

Replication + leader election provide fault tolerance, ACKs control delivery durability, and partition-based ordering guides key design.
