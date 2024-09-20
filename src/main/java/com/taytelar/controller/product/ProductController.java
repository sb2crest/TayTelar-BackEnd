package com.taytelar.controller.product;

import com.taytelar.request.product.AddProductRequest;
import com.taytelar.response.SuccessResponse;
import com.taytelar.response.product.AddProductResponse;
import com.taytelar.response.product.ProductResponse;
import com.taytelar.service.service.product.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    /**
     * Endpoint to add a new product to the inventory.

     * This endpoint accepts a request to create and add a new product, including its details such as name, description, material type, pattern, price, stock quantities, and associated images and video. The product is also associated with a category and subcategory.

     * The request payload should be submitted as a multipart form data, including:
     * - **Product Details**: Product name, description, material type, pattern, price, stock quantities, etc.
     * - **Images**: A map of image files with associated priority values.
     * - **Video**: A video file related to the product.

     * The method processes the request using the `ProductService` to save the product information, upload images and videos to an S3 bucket, and associate the product with the provided category and subcategory.
     *
     * @param request The request object containing product details, images, and video.
     * @return ResponseEntity<SuccessResponse> - A ResponseEntity containing a `SuccessResponse` object with HTTP status 200 OK if the product is successfully added.
     *
     */
    @PostMapping("/addProduct")
    public ResponseEntity<AddProductResponse> addProduct(@Valid @RequestBody AddProductRequest request) {
        AddProductResponse response = productService.addProduct(request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    /**
     * Endpoint to upload images and a video for a specific product.

     * This endpoint allows uploading multiple images with associated priority values and a video file related to a product identified by its `productId`. The images and video are uploaded to an external storage (e.g., AWS S3), and their metadata is associated with the specified product.

     * **Request Parameters**:
     * - **productId**: The unique identifier of the product to which the files are associated.
     * - **images**: Array of image files to be uploaded.
     * - **imagePriorities**: Array of priority values corresponding to each image.
     * - **video**: The video file to be uploaded.

     * **Response**: A `SuccessResponse` object with HTTP status 200 OK if the files are successfully uploaded and processed.
     *
     * @param productId The unique identifier of the product.
     * @param images Array of image files.
     * @param imagePriorities Array of priority values for images.
     * @param video The video file.
     * @return ResponseEntity<SuccessResponse> - A `ResponseEntity` containing a `SuccessResponse` object with HTTP status 200 OK.
     */
    @PostMapping("/uploadFiles")
    public ResponseEntity<SuccessResponse> uploadProductFiles(
            @RequestParam("productId") String productId,
            @RequestParam("images") MultipartFile[] images,
            @RequestParam("imagePriorities") Integer[] imagePriorities,
            @RequestParam("video") MultipartFile video) {

        SuccessResponse response = productService.uploadProductFiles(productId, images, imagePriorities, video);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * Endpoint to retrieve all products along with their associated categories and subcategories.

     * This endpoint fetches a hierarchical structure of product data. It includes:
     * - **Categories**: Main product categories like "Pants," "Shirts," etc.
     * - **Subcategories**: Subdivisions within each category such as "Formal Pants," "Jeans," etc.
     * - **Products**: Detailed information about products listed under each subcategory, including name, description, material type, pattern, price, stock quantities, and URLs for images and videos.

     * **Response**: A `ProductResponse` object with HTTP status 200 OK containing the product and category data.
     *
     * @return ResponseEntity<ProductResponse> - A `ResponseEntity` containing the `ProductResponse` object with HTTP status 200 OK.
     */
    @GetMapping("getAllProducts")
    public ResponseEntity<ProductResponse> getAllProducts() {
        ProductResponse response = productService.getAllProducts();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
