package com.taytelar.entity.order;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "orders_item_data")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemEntity {

    @Id
    @Column(name = "order_item_id",nullable = false, unique = true)
    private String orderItemId;

    @Column(name = "product_id", nullable = false)
    private String productId;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "unit_price", nullable = false)
    private Double unitPrice;

    @Column(name = "total_amount", nullable = false)
    private Double totalAmount;

    @Column(name = "return_days_policy", nullable = false)
    private int returnDaysPolicy;

    @ManyToOne
    @JoinColumn(name = "order_id",nullable = false)
    private OrderEntity orderEntity;
}
