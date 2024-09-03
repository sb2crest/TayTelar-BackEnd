package com.taytelar.repository;

import com.taytelar.entity.payment.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<PaymentEntity,String> {
    @Query("SELECT p FROM PaymentEntity p WHERE p.razorPayOrderId = :razorPayOrderId")
    PaymentEntity findBookingIdByRazorPayOrderId(String razorPayOrderId);
}
