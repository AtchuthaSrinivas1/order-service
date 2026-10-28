# Basic understanding on the existence of different resilience patterns
1. Why do we need timeouts?
2. Why are timeouts not enough? -> Retry
3. Why can retries make things worse? -> Circuit Breaker
4. Why isn't circuit breaker enough? -> Fallback
5. Why can one slow service still exhaust resources? -> Bulkhead
6. How do we protect against abuse? -> Rate Limiter

# Connection Timeout
    How long to wait to establish a connection with the product service.

# Read Timeout
    How long to wait for product service to send a response after the connection is estabished.

# Fault Tolerance
    Fault Tolerance is a sytem's ability to continue operation properly without interruption when one or more of its components fails.

# Resilience4j
    Resilence4j is a lightweight fault tolerance library designed for functional programming. You can apply resiliency patterns such as timeout, retry, circuit breaker by using Resilence4j very quickly  


# High-Level Comparison between Resilience4j & Spring Retry
    Feature                                     Spring Retry                                                Resilience4j Retry
    -------------------------------------------------------------------------------------------------------------------------------   
    Best Used For                          Standard applications, simple tasks                          Complex, cloud-native microservices
    Configuration                          Handled mainly via code annotations                          Managed externally via .yaml or .properties
    Ecosystem Integration                  Plugs directly into Spring Data/Kafka                        Pairs smoothly with Circuit Breakers & Rate Limiters
    Reactive Support                       Primarily synchronous/blocking threads                       Built from the ground up for WebFlux & Reactive streams


1. Why did you choose Resilience4j instead of Spring Retry?
- Spring Retry is focused only on retrying failed operations. Our microservice needed a broader resilience strategy, including Circuit Breaker, Rate Limiter, Bulkhead, Time Limiter and Retry. Resilience4j provides all these patterns in one library, integrates well with Spring Boot and Micrometer for monitoring and is the preferred choice for modern Spring Boot microservices. 

# Circuit Breaker

    Request 1
        ↓
    Retry 3 times
        ↓
       Fail

    Request 2
        ↓
    Retry 3 times
        ↓
       Fail

    Request 3
        ↓
    Retry 3 times
        ↓
      Fail
    ..........

    1000 requests
        ↓
    3000 unnecessary calls
        
    - Retry actually makes the failing service receive even more traffic.
    - So circuit breaker says I already know the Product Service is down and I'm not going to call it again.
            - No network call
            - No waiting 
            - Fast Response

# Circuit Breaker States
## 1. CLOSED (Normal)
-    Everthing works
    
    Order
      ↓
    Product
      ↓
    SUCCESS

## 2. OPEN (Failure)
- Suppose 10 requests fail.
  - CB says stop, dont call product service anymore

        Order
         ↓
        Circuit Breaker
          ↓
        Immediately Fail
- No Feign call

## 3. HALF OPEN (Recovery Check)
- After sometime:
        
        wait 30 seconds
- CB allows one or a few requests through 
  - 
  - if they succeed:
         
        - Circuit closes again
  - if they fail:

        - Circuit opens again