package com.taytelar.entity.payment;

import com.taytelar.entity.order.OrderEntity;
import com.taytelar.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "payment_data")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentEntity {

    @Id
    @Column(name = "payment_id", nullable = false, unique = true)
    private String paymentId;

    @Column(name = "user_id",nullable = false)
    private String userId;

    @Column(name = "razorpay_order_id")
    private String razorPayOrderId;

    @Column(name = "razorpay_payment_id")
    private String razorPayPaymentId;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false)
    private PaymentStatus paymentStatus;

    @Column(name = "total_amount", nullable = false)
    private Double totalAmount;

    @Column(name = "payment_date")
    private LocalDateTime paymentDate;

    @Column(name = "payment_method", nullable =false)
    private String paymentMethod;

    @OneToOne
    @JoinColumn(name = "order_id", referencedColumnName = "order_id")
    private OrderEntity orderEntity;
}
