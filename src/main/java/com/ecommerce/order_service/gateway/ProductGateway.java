package com.ecommerce.order_service.gateway;

import com.ecommerce.order_service.dto.request.ReduceStockRequest;
import com.ecommerce.order_service.dto.response.ProductResponse;
import com.ecommerce.order_service.exception.InvalidProductDataException;
import com.ecommerce.order_service.exception.OutOfStockException;
import com.ecommerce.order_service.exception.ProductNotFoundException;
import com.ecommerce.order_service.exception.ProductServiceUnavailableException;
import com.ecommerce.order_service.integration.ProductClient;
import feign.FeignException;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * This is called Abstraction layer or Adapter Pattern.
 * Abstraction - Hides Feign implementation details from the service layer
 * Exception Translation - Converts Feign-specific exceptions to domain exceptions
 * Comprehensive Logging - Logs all operations and errors for debugging
 * Error Categorization - Handles 400, 404, and 50x errors separately
 * Reusability - Can be easily replaced with WebClient, gRPC, or Kafka without changing service code
 * <p>
 * This gateway acts as a protective layer between your business logic and external API calls.
 */

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductGateway {

    private final ProductClient productClient;

//    @RateLimiter(name = "productServiceRateLimiter")
    @Bulkhead(name = "productServiceBulkhead")
    @CircuitBreaker(name = "productServiceCB")
    @Retry(name = "productServiceRetry")
    public ProductResponse getProductById(Long productId) {
        try {
            log.debug("Fetching product with id={}", productId);
            ProductResponse productResponse = productClient.getProduct(productId);
            log.debug("Successfully fetched product with id={}", productId);

            return productResponse;
        } catch (FeignException.BadRequest e) {
            log.error("Product validation failed for productId={}: {}", productId, e.getMessage());
            throw new InvalidProductDataException(productId);
        } catch (FeignException.NotFound e) {
            log.error("Product not found with id={}", productId);
            throw new ProductNotFoundException(productId);
        } catch (FeignException e) {
            log.error("Error checking product availability for productId={}: {}", productId, e.getMessage());
            throw new ProductServiceUnavailableException("Unable to verify product availability. Please try again later.");
        }
    }

    public void reduceStock(Long productId, ReduceStockRequest request) {
        try {
            log.debug("Reducing stock for product with id={}, request={}", productId, request);
            productClient.reduceStock(productId, request);
            log.debug("Successfully reduced stock for product with id={}", productId);
        } catch (FeignException.BadRequest e) {
            log.error("Invalid reduce stock request for productId={}: {}", productId, e.getMessage());
            throw new InvalidProductDataException(productId);
        } catch (FeignException.NotFound e) {
            log.error("Product not found for reduce stock with id={}", productId);
            throw new ProductNotFoundException(productId);
        } catch (FeignException.Conflict e) {
            log.error("Product went out of stock with id={}", productId);
            throw new OutOfStockException(productId, request.getQuantity());
        } catch (FeignException e) {
            log.error("Error updating product stock for productId={}: {}", productId, e.getMessage());
            throw new ProductServiceUnavailableException("Unable to update product stock. Please try again later.");
        }
    }
}
