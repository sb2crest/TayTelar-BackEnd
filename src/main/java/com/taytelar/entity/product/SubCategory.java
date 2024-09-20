package com.taytelar.entity.product;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
public class SubCategory {

    @Id
    @Column(name = "sub_category_id")
    private String subCategoryId;

    @Column(name = "sub_category_name")
    private String subCategoryName;

    @Column(name = "sub_category_description")
    private String subCategoryDescription;

    @ManyToOne
    @JoinColumn(name = "category_id",referencedColumnName = "category_id")
    private Category category;

    @OneToMany(mappedBy ="subCategory",cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Product> products;
}
