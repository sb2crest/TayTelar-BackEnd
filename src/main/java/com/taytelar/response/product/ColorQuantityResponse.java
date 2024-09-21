package com.taytelar.response.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ColorQuantityResponse {
    private String color;
    private Integer quantity;
}
