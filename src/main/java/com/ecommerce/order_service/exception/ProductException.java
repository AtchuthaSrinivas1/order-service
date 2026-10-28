package com.ecommerce.order_service.exception;

import lombok.Getter;

@Getter
public class ProductException extends RuntimeException{
    private final Long productId;

    public ProductException(String message) {
        super(message);
        this.productId = null;
    }

    public ProductException(String message, Throwable cause) {
        super(message, cause);
        this.productId = null;
    }

    public ProductException(String message, Long productId) {
        super(message);
        this.productId = productId;
    }

    public ProductException(String message, Long productId, Throwable cause) {
        super(message, cause);
        this.productId = productId;
    }
}
