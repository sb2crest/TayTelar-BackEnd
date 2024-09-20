package com.taytelar.response.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubCategoryResponse {

    private String subCategoryId;
    private String subCategoryName;
    private String subCategoryDescription;
    private List<ProductDataResponse> productDataResponses;
}