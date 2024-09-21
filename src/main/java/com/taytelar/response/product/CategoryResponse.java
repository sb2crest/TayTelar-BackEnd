package com.taytelar.response.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryResponse {

    private String categoryId;
    private String categoryName;
    private String categoryDescription;
    private List<SubCategoryResponse> subCategoryResponses;
}
