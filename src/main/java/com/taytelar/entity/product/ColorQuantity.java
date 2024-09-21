package com.taytelar.entity.product;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "color_quantity_data")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ColorQuantity {

    @Id
    @Column(name = "color_quantity_id")
    private String colorQuantityId;

    @Column(name = "color")
    private String color;

    @Column(name = "color_code")
    private String colorCode;

    @Column(name = "quantity")
    private Integer quantity;

    @ManyToOne
    @JoinColumn(name = "stock_id", referencedColumnName = "stock_id")
    private StockQuantity stockQuantity;

}
