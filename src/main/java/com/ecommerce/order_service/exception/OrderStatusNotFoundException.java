package com.ecommerce.order_service.exception;

public class OrderStatusNotFoundException extends RuntimeException {

    public OrderStatusNotFoundException(String statusName) {
        super("Order status '" + statusName + "' not found in the system");
    }
}

