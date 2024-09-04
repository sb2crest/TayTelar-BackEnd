package com.taytelar.request.order;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.List;

@Data
public class OrderRequest {

    @NotBlank(message = "User ID cannot be blank")
    private String userId;

    @NotNull(message = "Total amount cannot be null")
    @Positive(message = "Total amount must be positive")
    private Double totalAmount;

    @NotNull(message = "Order items cannot be null")
    @Valid
    private List<OrderItemRequest> orderItemRequests;

    @NotBlank(message = "payment method cannot be null")
    private String paymentMethod;

}
