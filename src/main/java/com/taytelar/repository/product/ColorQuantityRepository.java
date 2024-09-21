package com.taytelar.repository.product;

import com.taytelar.entity.product.ColorQuantity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ColorQuantityRepository extends JpaRepository<ColorQuantity, String> {
}
