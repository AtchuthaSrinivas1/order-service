package com.ecommerce.order_service.exception;

import lombok.Getter;

@Getter
public class ProductNotFoundException extends ProductException {
    public ProductNotFoundException(Long productId) {
        super(String.format("Product not found with id: %d", productId), productId);
    }
}

