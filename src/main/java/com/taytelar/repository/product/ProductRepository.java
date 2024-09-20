package com.taytelar.repository.product;

import com.taytelar.entity.product.Product;
import com.taytelar.entity.product.SubCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {
    List<Product> findBySubCategory(SubCategory subCategoryId);

    Product findByProductId(String productId);
}
