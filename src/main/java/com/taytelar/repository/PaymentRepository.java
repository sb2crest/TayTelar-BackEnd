package com.taytelar.repository;

import com.taytelar.entity.payment.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<PaymentEntity,String> {
    PaymentEntity findByRazorPayOrderId(String razorPayOrderId);
}
