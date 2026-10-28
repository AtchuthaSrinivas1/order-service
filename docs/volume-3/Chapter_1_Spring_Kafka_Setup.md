# Chapter 1 - Spring Kafka Setup

## Why Spring Kafka?

Spring Boot alone does not provide Kafka producer/consumer support.  
`spring-kafka` adds:

- `KafkaTemplate` for publishing messages
- `@KafkaListener` for consuming messages
- Producer and consumer factories
- Serializer/deserializer integration
- Error handling and retry support

## Maven Dependency

```xml
<dependency>
  <groupId>org.springframework.kafka</groupId>
  <artifactId>spring-kafka</artifactId>
</dependency>
```

## Bootstrap Servers

`bootstrap-servers` is the initial broker list used by the client to discover full cluster metadata.

```yaml
spring:
  kafka:
    bootstrap-servers: localhost:9092
```

### Internal Flow

Spring Boot client -> bootstrap broker -> metadata fetch -> direct communication with partition leaders.

### Environment Examples

- Local: `localhost:9092`
- Docker Compose: `kafka:9092`
- Production: `broker1:9092,broker2:9092,broker3:9092`

## Order Service Context

In this e-commerce flow:

- **Synchronous path (REST):** Order Service calls Product Service to validate product/stock.
- **Asynchronous path (Kafka):** After order creation, Order Service publishes `OrderCreated` for downstream consumers.

## Interview Notes

1. **Why is it called bootstrap server?**  
   It is only the first contact point. After metadata is fetched, the client talks to the correct leader broker directly.
2. **Why is one broker enough in bootstrap list?**  
   One is technically enough to fetch metadata, but multiple are recommended for high availability.
3. **What if one bootstrap broker is down?**  
   If multiple bootstrap addresses are configured, the client tries others.

## Summary

Bootstrap servers are discovery endpoints, not permanent single communication targets.
