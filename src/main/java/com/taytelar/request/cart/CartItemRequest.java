package com.taytelar.request.cart;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class CartItemRequest {

    private String cartItemId;

    @NotBlank(message = "product id cannot be blank")
    private String productId;

    @NotBlank(message = "product name cannot be blank")
    private String productName;

    @NotNull(message = "Size cannot be null")
    @Min(value = 1, message = "Size must be greater than 0")
    private Integer productSize;

    @NotBlank(message = "Color cannot be blank")
    private String productColor;

    @NotNull(message = "Quantity cannot be null")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    @NotNull(message = "Price cannot be null")
    @Positive(message = "Price must be positive")
    private Double price;

    @NotNull(message = "Product Image Url cannot be blank")
    private String productImageUrl;
}
