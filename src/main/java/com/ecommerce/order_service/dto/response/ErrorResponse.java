package com.ecommerce.order_service.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Builder
@Schema(description = "Standard error response")
public class ErrorResponse {

    @Schema(description = "HTTP status code", example = "404")
    private int status;

    @Schema(description = "Short error code", example = "ORDER_NOT_FOUND")
    private String errorCode;

    @Schema(description = "Human-readable message", example = "Order with id 99 not found")
    private String message;

    @Schema(description = "Request path", example = "/orders/99")
    private String path;

    @Schema(description = "Timestamp of the error")
    private LocalDateTime timestamp;

    @Schema(description = "Field-level validation errors")
    private Map<String, String> validationErrors;
}

