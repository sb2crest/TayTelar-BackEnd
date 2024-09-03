package com.taytelar.request.order;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderItemRequest {

    @NotBlank(message = "Product ID cannot be blank")
    private String productId;

    @NotNull(message = "Quantity cannot be null")
    @Positive(message = "Quantity must be positive")
    private Integer quantity;

    @NotNull(message = "Total amount cannot be null")
    @Positive(message = "Total amount must be positive")
    private Double totalAmount;
}
