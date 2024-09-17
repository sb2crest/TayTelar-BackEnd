package com.taytelar.entity.order;

import com.taytelar.entity.payment.PaymentEntity;
import com.taytelar.entity.user.UserEntity;
import com.taytelar.enums.DeliveryStatus;
import com.taytelar.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders_data")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class OrderEntity {

    @Id
    @Column(name = "order_id", nullable = false, unique = true)
    private String orderId;

    @Column(name = "order_date", nullable = false)
    private LocalDateTime orderDate;

    @Column(name = "total_amount", nullable = false)
    private Double totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status", nullable = false)
    private OrderStatus orderStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "delivery_status", nullable = false)
    private DeliveryStatus deliveryStatus;

    @Column(name = "delivery_date")
    private LocalDateTime deliveryDate;

    @Column(name = "payment_method")
    private String paymentMethod;

    @OneToMany(mappedBy = "orderEntity", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<OrderItemEntity> orderItemEntities;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    @ToString.Exclude
    private UserEntity userEntity;

    @OneToOne(mappedBy = "orderEntity", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    private PaymentEntity paymentEntity;

}
