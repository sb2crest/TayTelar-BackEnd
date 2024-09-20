package com.taytelar.service.serviceimplementation.cart;

import com.taytelar.entity.cart.CartEntity;
import com.taytelar.entity.cart.CartItemEntity;
import com.taytelar.exception.cart.CartItemNotFoundException;
import com.taytelar.repository.cart.CartRepository;
import com.taytelar.request.cart.CartItemRequest;
import com.taytelar.request.cart.CartRequest;
import com.taytelar.response.SuccessResponse;
import com.taytelar.response.cart.CartItemResponse;
import com.taytelar.response.cart.CartResponse;
import com.taytelar.service.service.cart.CartService;
import com.taytelar.util.Constants;
import com.taytelar.util.Generator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartServiceImplementation implements CartService {

    private final CartRepository cartRepository;
    private final Generator generator;

    @Override
    public SuccessResponse addToCart(CartRequest cartRequest) {

        CartEntity cartEntity = cartRepository.findByUserId(cartRequest.getUserId());

        if (cartEntity == null) {
            CartEntity cart = new CartEntity();
            cart.setCartId(generator.generateId(Constants.CART_ID));
            cart.setUserId(cartRequest.getUserId());
            cart.setCartItemEntityList(getCartItemEntityList(cartRequest.getCartItemRequests()));
            cartRepository.save(cart);
        } else {
            cartEntity.getCartItemEntityList().addAll(getCartItemEntityList(cartRequest.getCartItemRequests()));
            cartRepository.save(cartEntity);
        }

        SuccessResponse successResponse = new SuccessResponse();
        successResponse.setMessage(Constants.ADDED_TO_CART_SUCCESSFULLY);
        successResponse.setStatusCode(HttpStatus.OK.value());

        return successResponse;
    }

    @Override
    public SuccessResponse updateCartItem(CartRequest cartRequest) {

        CartEntity cartEntity = cartRepository.findByUserId(cartRequest.getUserId());

        if (cartEntity != null) {
            List<CartItemEntity> cartItems = cartEntity.getCartItemEntityList();
            for (CartItemRequest cartItemRequest : cartRequest.getCartItemRequests()) {
                String cartItemId = cartItemRequest.getCartItemId();

                for (CartItemEntity cartItem : cartItems) {
                    if (cartItem.getCartItemId().equals(cartItemId)) {
                        cartItem.setProductId(cartItemRequest.getProductId());
                        cartItem.setProductName(cartItemRequest.getProductName());
                        cartItem.setProductSize(cartItemRequest.getProductSize());
                        cartItem.setProductColor(cartItemRequest.getProductColor());
                        cartItem.setQuantity(cartItemRequest.getQuantity());
                        cartItem.setPrice(cartItemRequest.getPrice());
                        break;
                    }
                }
            }
            cartRepository.save(cartEntity);

            SuccessResponse successResponse = new SuccessResponse();
            successResponse.setMessage(Constants.CART_ITEM_UPDATE);
            successResponse.setStatusCode(HttpStatus.OK.value());

            return successResponse;
        } else {
            log.error("CartServiceImplementation, updateCart, Cart not found for user: {}", cartRequest.getUserId());
            throw new CartItemNotFoundException("Cart not found for user: " + cartRequest.getUserId());
        }
    }

    @Override
    public SuccessResponse deleteCartItem(CartRequest cartRequest) {
        CartEntity cartEntity = cartRepository.findByUserId(cartRequest.getUserId());

        if (cartEntity != null) {
            List<CartItemEntity> cartItems = cartEntity.getCartItemEntityList();
            for (CartItemRequest cartItemRequest : cartRequest.getCartItemRequests()) {
                String cartItemId = cartItemRequest.getCartItemId();
                cartItems.removeIf(cartItem -> cartItem.getCartItemId().equals(cartItemId));
            }
            cartEntity.setCartItemEntityList(cartItems);
            cartRepository.save(cartEntity);

            SuccessResponse successResponse = new SuccessResponse();
            successResponse.setMessage(Constants.CART_ITEM_DELETED);
            successResponse.setStatusCode(HttpStatus.OK.value());

            return successResponse;

        } else {
            log.error("CartServiceImplementation, updateCart," + Constants.CART_NOT_FOUND + ": {}", cartRequest.getUserId());
            throw new CartItemNotFoundException(Constants.CART_NOT_FOUND + ": " + cartRequest.getUserId());
        }
    }

    @Override
    public CartResponse getCartItems(String userId) {
        CartEntity cartEntity = cartRepository.findByUserId(userId);

        if(cartEntity != null) {
            CartResponse cartResponse = new CartResponse();
            cartResponse.setUserId(cartEntity.getUserId());
            cartResponse.setCartId(cartEntity.getCartId());
            cartResponse.setCartItemResponses(getCartItemResponse(cartEntity.getCartItemEntityList()));

            return cartResponse;

        } else {
            log.error("CartServiceImplementation, updateCart," + Constants.CART_NOT_FOUND + ": {}", userId);
            throw new CartItemNotFoundException(Constants.CART_NOT_FOUND + ": " + userId);
        }
    }

    private List<CartItemResponse> getCartItemResponse(List<CartItemEntity> cartItemEntityList) {
        return cartItemEntityList.stream()
                .map(cartItemEntity -> {
                    CartItemResponse cartItemResponse = new CartItemResponse();
                    cartItemResponse.setCartItemId(cartItemEntity.getCartItemId());
                    cartItemResponse.setProductId(cartItemEntity.getProductId());
                    cartItemResponse.setProductName(cartItemEntity.getProductName());
                    cartItemResponse.setProductSize(cartItemEntity.getProductSize());
                    cartItemResponse.setProductColor(cartItemEntity.getProductColor());
                    cartItemResponse.setQuantity(cartItemEntity.getQuantity());
                    cartItemResponse.setPrice(cartItemEntity.getPrice());
                    return cartItemResponse;
                })
                .toList();
    }

    private List<CartItemEntity> getCartItemEntityList(List<CartItemRequest> cartItemRequests) {
        return cartItemRequests.stream()
                .map(cartItemRequest -> {
                    CartItemEntity itemEntity = new CartItemEntity();
                    itemEntity.setCartItemId(generator.generateId(Constants.CART_ITEM_ID));
                    itemEntity.setProductId(cartItemRequest.getProductId());
                    itemEntity.setProductName(cartItemRequest.getProductName());
                    itemEntity.setProductSize(cartItemRequest.getProductSize());
                    itemEntity.setProductColor(cartItemRequest.getProductColor());
                    itemEntity.setQuantity(cartItemRequest.getQuantity());
                    itemEntity.setPrice(cartItemRequest.getPrice());
                    return itemEntity;
                })
                .toList();
    }
}
