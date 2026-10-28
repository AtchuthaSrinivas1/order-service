# Chapter 4 - Keys, Offsets, and Retention

## Message Key

The key decides partition placement.

Why use keys:

- Route related events together (example: same `orderId`)
- Preserve ordering for that entity inside one partition

Without keys, partitioning is typically round-robin and related events may split across partitions.

## Offset

Offset is the index of a record inside a partition.

Consumer behavior:

- Reads records in offset order
- Commits offsets to store progress
- Resumes from last committed offset after restart

## Retention

Kafka keeps records based on configured retention policies (time/size), not because they are consumed once.

Why this matters:

- Multiple groups can consume at different speeds
- Replay is possible for recovery and reprocessing

## Replay

Consumers can re-read old records by resetting offsets.

Typical uses:

- Rebuild derived state
- Backfill analytics
- Recover after consumer bugs

## Summary

Keys influence partition and entity-level ordering, offsets track progress, and retention enables durability plus replay.
