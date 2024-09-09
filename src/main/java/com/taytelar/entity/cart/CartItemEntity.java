package com.taytelar.entity.cart;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Field;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CartItemEntity {

    @Field("cart_item_id")
    private String cartItemId;

    @Field("product_id")
    private String productId;

    @Field("product_name")
    private String productName;

    @Field("product_size")
    private Integer productSize;

    @Field("product_color")
    private String productColor;

    @Field("quantity")
    private Integer quantity;

    @Field("price")
    private Double price;
}
