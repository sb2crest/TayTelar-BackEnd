package com.taytelar.service.service.cart;

import com.taytelar.request.cart.CartRequest;
import com.taytelar.response.SuccessResponse;
import com.taytelar.response.cart.CartResponse;

public interface CartService {
    SuccessResponse addToCart(CartRequest cartRequest);

    SuccessResponse updateCartItem(CartRequest cartRequest);

    SuccessResponse deleteCartItem(CartRequest cartRequest);

    CartResponse getCartItems(String userId);
}
