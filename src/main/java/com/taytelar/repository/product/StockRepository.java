package com.taytelar.repository.product;

import com.taytelar.entity.product.StockQuantity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StockRepository extends JpaRepository<StockQuantity, String> {
}
