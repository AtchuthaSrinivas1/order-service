package com.ecommerce.order_service.exception;

public class ProductServiceUnavailableException extends ProductException{
    public ProductServiceUnavailableException(String message) {
        super(message);
    }

    public ProductServiceUnavailableException() {
        super("Product Service Unavailable");
    }
}
