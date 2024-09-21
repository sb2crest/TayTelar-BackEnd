package com.taytelar.entity.product;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.Map;

@Entity
@Table(name = "product_data")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Product {

    @Id
    @Column(name = "product_id")
    private String productId;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "product_description")
    private String productDescription;

    @Column(name = "product_material_type")
    private String productMaterialType;

    @Column(name = "product_pattern")
    private String productPattern;

    @Column(name = "product_price")
    private Double productPrice;

    @ElementCollection
    @CollectionTable(name = "product_images", joinColumns = @JoinColumn(name = "product_id"))
    @MapKeyColumn(name = "image_url")
    @Column(name = "image_urls")
    private Map<String, Integer> imageUrls;

    @Column(name = "video_url")
    private String videoUrl;

    @OneToMany(mappedBy = "product",cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<StockQuantity> stockQuantities;

    @ManyToOne
    @JoinColumn(name = "sub_category_id", referencedColumnName ="sub_category_id")
    private SubCategory subCategory;
}