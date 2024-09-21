package com.taytelar.response.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDataResponse {

    private String productId;
    private String productStatus;
    private String productName;
    private String productDescription;
    private String productMaterialType;
    private String productPattern;
    private Double productPrice;
    private List<StockQuantityResponse> stockQuantityResponseList;
    private Map<String, Integer> images;
    private String video;
}
