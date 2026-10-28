package com.ecommerce.order_service.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Response object containing product details")
public class ProductResponse {

    @Schema(description = "Unique identifier of the product", example = "1")
    private Long id;

    @Schema(description = "Name of the product", example = "iPhone 15")
    private String name;

    @Schema(description = "Description of the product", example = "Latest Apple smartphone with A17 Pro chip")
    private String description;

    @Schema(description = "Price of the product", example = "80000")
    private Integer price;

    @Schema(description = "Available stock quantity", example = "10")
    private Integer stock;

    @Schema(description = "Timestamp when the product was created")
    private LocalDateTime createdAt;

    @Schema(description = "Timestamp when the product was last modified")
    private LocalDateTime modifiedAt;
}

