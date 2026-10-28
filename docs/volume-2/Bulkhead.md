## Bulkhead
2. Why do we need Bulkhead?
   - If a downstream service like Product Service becomes slow, Order Service threads wait for its
   response. With enough concurrent requests, all threads become blocked. Even though Order
   Service is healthy, it cannot serve new requests. This is resource exhaustion.


2. Doesn't Circuit Breaker solve this?
   - No. Circuit Breaker opens only after observing failures. Before it opens, many requests may already
   be blocked. Bulkhead protects resources immediately by limiting concurrent access.


3. How Bulkhead Works
   - **Example**: Server has 200 request threads. Configure only 20 concurrent calls to Product Service. If
   Product Service hangs, only those 20 threads are occupied while the remaining threads continue
   serving login, health, and other APIs.


4. Benefits
   - • Prevents thread/resource exhaustion.
   - • Keeps unrelated endpoints responsive.
   - • Improves system stability.
   - • Complements Timeout, Retry and Circuit Breaker.


5. Bulkhead vs Circuit Breaker
   - **Circuit Breaker:** Stops calling an unhealthy service after enough failures.
   - **Bulkhead:** Prevents one dependency from consuming all application resources.


6. Types
   - **Semaphore Bulkhead**: Limits concurrent executions. Best for synchronous Feign calls.
   - **Thread Pool Bulkhead**: For asynchronous. Uses a dedicated thread pool for isolated execution.


7. Interview Question
   - **Q: If Circuit Breaker exists, why Bulkhead?**
   - **A:** Circuit Breaker protects against repeatedly calling an unhealthy dependency after failures are
   detected. Bulkhead protects application resources before that point by isolating threads or
   connections so one slow dependency cannot block unrelated requests.

   - **Q: Doesn't Bulkhead reduce throughput by limiting concurrency?**
   - **A:** Yes, it intentionally limits concurrency for a protected resource. This is a trade-off.
   Instead of allowing one slow dependency to consume all application threads and make the entire service
   unavailable, Bulkhead isolates that dependency.


8. Key Takeaway
   - Timeout limits waiting time. Retry handles transient failures. Circuit Breaker stops repeated calls to
   unhealthy services. Bulkhead protects resources from exhaustion. Together they build resilient
   microservices


9. Ordering
    - **Conceptual (Architectural) Order**
      - request -> Bulkhead -> Retry -> Circuit Breaker -> Product Service
    - **why ?** 
    - **A:** because from design perspective 
      - Bulkhead -> Do i have capacity?
      - Retry -> If the call fails temporarily, should I try again?
      - Circuit Breaker -> Should I stop calling this service altogether?

   - **Actual Resilience4j Execution Order**
       - Retry -> Circuit Breaker -> Bulkhead -> Product Service (Feign)
   - **why ?**
   - **A:** because each retry attempt acquires and releases a Bulkhead permit independently.


# Rate Limiter

- **Bulkhead** protects your application's resources.
- **Rate Limiter** protects your application from excessive traffic.

-  **Q: If you already use Bulkhead, why do you still need Rate Limiter?**
- **A:** Bulkhead limits the number of concurrent operations using application resources 
    and rejects requests when capacity is exhausted. However, clients can continue sending
    unlimited requests, causing unnecessary load. A Rate Limiter controls how many requests
    are accepted from a client within a time window, preventing excessive traffic from reaching
    the application in the first place.