package com.taytelar.entity.product;

import com.taytelar.enums.ProductAttributeType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class ProductAttribute {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "productAttributeId")
    private Long productAttributeId;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private ProductAttributeType type;

    @Column(name = "value", nullable = false)
    private String value;

    @Column(name = "createdAt", nullable = false, updatable = false)
    private LocalDateTime createdAt;


}
