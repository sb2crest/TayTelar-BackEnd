package com.taytelar.response.cart;

import lombok.Data;

@Data
public class CartItemResponse {

    private String cartItemId;
    private String productId;
    private String productName;
    private Integer productSize;
    private String productColor;
    private Integer quantity;
    private Double price;
    private String productImagesUrl;
}
