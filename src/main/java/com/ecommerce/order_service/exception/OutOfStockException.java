package com.ecommerce.order_service.exception;

public class OutOfStockException extends RuntimeException {
    public OutOfStockException(Long productId, int requestedQuantity, int availableStock) {
        super(String.format("Product with id=%d is out of stock. Requested: %d, Available: %d",
                productId, requestedQuantity, availableStock));
    }

    public OutOfStockException(Long productId, int requestedQuantity) {
        super(String.format("Product with id=%d is out of stock. Requested: %d",
                productId, requestedQuantity));
    }

    public OutOfStockException(String message) {
        super(message);
    }
}

