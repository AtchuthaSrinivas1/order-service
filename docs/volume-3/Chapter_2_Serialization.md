# Chapter 2 - Serialization

## Why Serialization?

Kafka stores and transfers only bytes (`byte[]`), so Java objects must be converted before sending.

## Key Serializer

```yaml
spring:
  kafka:
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
```

Flow: `String` -> `byte[]` -> Kafka

Keys determine partition selection.

## Value Serializer

```yaml
spring:
  kafka:
    producer:
      value-serializer: org.springframework.kafka.support.serializer.JsonSerializer
```

Flow: `OrderCreatedEvent` -> JSON -> `byte[]` -> Kafka

Consumer side does the reverse:

```yaml
spring:
  kafka:
    consumer:
      key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      value-deserializer: org.springframework.kafka.support.serializer.JsonDeserializer
```

`byte[]` -> JSON -> Java object

## Common Serializer Choices

- `StringSerializer`
- `IntegerSerializer`
- `LongSerializer`
- `JsonSerializer`

Schema-based (common in larger systems):

- Avro
- Protobuf

## Common Mistakes

- Wrong serializer/deserializer pairing between producer and consumer
- Sending custom Java objects without JSON (or schema) serialization
- Missing trusted packages config for JSON deserialization in Spring Kafka

## Interview Questions

1. Why does Kafka store bytes instead of Java objects?
2. Why is `JsonSerializer` a good default for learning projects?
3. Can Kafka store Java objects directly?

## Summary

Serialization converts Java objects into bytes for Kafka.
Deserialization reconstructs the object on the consumer side.
