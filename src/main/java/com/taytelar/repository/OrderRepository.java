package com.taytelar.repository;

import com.taytelar.entity.order.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity,String> {
    @Query("SELECT o FROM OrderEntity o WHERE o.orderId = : orderId")
    OrderEntity findByOrderId(String orderId);
}
