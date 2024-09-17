package com.taytelar.entity.cart;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;
@Document(collection = "cart_data")
@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class CartEntity {

    @Field("user_id")
    private String userId;

    @Id
    @Field("cart_id")
    private String cartId;

    @Field("cart_items")
    private List<CartItemEntity> cartItemEntityList;

}
