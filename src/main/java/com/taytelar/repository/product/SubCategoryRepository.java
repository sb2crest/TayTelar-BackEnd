package com.taytelar.repository.product;

import com.taytelar.entity.product.Category;
import com.taytelar.entity.product.SubCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubCategoryRepository extends JpaRepository<SubCategory, String> {
    Optional<SubCategory> findBySubCategoryName(String subCategoryName);

    List<SubCategory> findByCategory(Category categoryId);
}
