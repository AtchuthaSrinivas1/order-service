package com.ecommerce.order_service.exception;

public class InvalidProductDataException extends ProductException {
    public InvalidProductDataException(Long productId) {
        super(String.format("Invalid product:  %d", productId), productId);

    }
}

