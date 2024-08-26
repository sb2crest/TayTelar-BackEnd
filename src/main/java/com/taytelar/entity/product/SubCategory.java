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
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "subCategoryId")
    private Long subCategoryId;

    @Column(name = "subCategoryName")
    private String subCategoryName;

    @Column(name = "subCategoryDescription")
    private String subCategoryDescription;

    @ManyToOne
    @JoinColumn(name = "categoryId",referencedColumnName = "categoryId")
    private Category category;

    @OneToMany(mappedBy ="subCategory",cascade = CascadeType.ALL)
    private List<Product> products;
}
