package com.ecommerce.order_service.integration;

import com.ecommerce.order_service.config.FeignConfiguration;
import com.ecommerce.order_service.dto.request.ReduceStockRequest;
import com.ecommerce.order_service.dto.response.ProductResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "product-service",
        url = "${product.service.url}",
        configuration = FeignConfiguration.class
)
public interface ProductClient {

    @GetMapping("/api/products/{id}")
    ProductResponse getProduct(@PathVariable("id") Long id);

    @PutMapping("/api/products/{id}/reduce-stock")
    ProductResponse reduceStock(@PathVariable("id") Long id, @RequestBody ReduceStockRequest request);
}
