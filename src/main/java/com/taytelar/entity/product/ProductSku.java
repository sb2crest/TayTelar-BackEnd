package com.taytelar.entity.product;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class ProductSku {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long productSkuId;

    @Column(name = "sku")
    private String sku;

    @Column(name = "price")
    private Double price;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
}
