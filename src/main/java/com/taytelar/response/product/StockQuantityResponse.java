package com.taytelar.response.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockQuantityResponse {

    private Integer size;
    private List<ColorQuantityResponse> colorQuantityResponses;
}
