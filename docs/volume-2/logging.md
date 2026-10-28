## Structured Logging:
    - 2026-07-22 11:10:01
    - Service=OrderService
    - Operation=CreateOrder
    - CorrelationId=abc1234
    - Calling=ProductService
    - ProductId=123
    - Error=Connection Refused

## Correlation ID

## Distributed Tracing
    - Tools:
        - Zipkin
        - Jaeger
        - OpenTelemetry

    - Instead of reading logs... you see as below

    Client
      ↓
    Order (20 ms)
      ↓
    Product (50 ms)
      ↓
    Inventory (FAILED)
      ↓
    Payment (Not Called)

## Centralized Logging
    - Instead of checking each service logs, everything goes to ElasticSearch -> Kibana or Grafana Loki. then u can search with correlation Id. All logs appear.

