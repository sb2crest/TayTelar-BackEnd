package com.taytelar.entity.product;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "stock_data")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class StockQuantity {

    @Id
    @Column(name = "stock_id")
    private String stockId;

    @Column(name = "size")
    private Integer size;

    @OneToMany(mappedBy = "stockQuantity",cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ColorQuantity> colorQuantities;

    @ManyToOne
    @JoinColumn(name = "product_id", referencedColumnName="product_id")
    private Product product;
}
