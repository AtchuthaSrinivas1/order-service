package com.ecommerce.order_service.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
@Schema(description = "Order details response")
public class OrderResponse {

    @Schema(description = "Unique order ID", example = "1")
    private Long id;

    @Schema(description = "Product ID", example = "101")
    private Long productId;

    @Schema(description = "Product name", example = "Apple iPhone 15 Pro")
    private String productName;

    @Schema(description = "Unit price", example = "999.99")
    private BigDecimal price;

    @Schema(description = "Quantity ordered", example = "2")
    private Integer quantity;

    @Schema(description = "Total amount (price × quantity)", example = "1999.98")
    private BigDecimal totalAmount;

    @Schema(description = "Current order status", example = "PENDING")
    private String status;

    @Schema(description = "Timestamp when order was created")
    private LocalDateTime createdAt;

    @Schema(description = "Timestamp when order was last updated")
    private LocalDateTime updatedAt;
}

