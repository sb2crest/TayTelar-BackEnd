package com.taytelar.service.service.product;

import com.taytelar.request.product.AddProductRequest;
import com.taytelar.response.SuccessResponse;
import com.taytelar.response.product.AddProductResponse;
import com.taytelar.response.product.ProductResponse;
import org.springframework.web.multipart.MultipartFile;

public interface ProductService {
    AddProductResponse addProduct(AddProductRequest request);

    ProductResponse getAllProducts();

    SuccessResponse uploadProductFiles(String productId, MultipartFile[] images, Integer[] imagePriorities,MultipartFile video);
}
