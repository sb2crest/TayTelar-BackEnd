package com.taytelar.response.cart;

import lombok.Data;

import java.util.List;

@Data
public class CartResponse {

    private String userId;

    private String cartId;

    private List<CartItemResponse> cartItemResponses;
}
