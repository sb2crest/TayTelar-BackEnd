package com.taytelar.request.product;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockQuantityRequest {

    @NotNull(message = "Size cannot be blank")
    private Integer size;

    @NotNull(message = "Color-Quantity list cannot be null")
    @Valid
    private List<ColorQuantityRequest> colorQuantities;
}
