package com.taytelar.service.serviceimplementation.product;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taytelar.entity.product.*;
import com.taytelar.exception.product.ProductNotFoundException;
import com.taytelar.exception.product.S3UploadException;
import com.taytelar.repository.product.CategoryRepository;
import com.taytelar.repository.product.ProductRepository;
import com.taytelar.repository.product.SubCategoryRepository;
import com.taytelar.request.product.AddProductRequest;
import com.taytelar.request.product.ColorQuantityRequest;
import com.taytelar.request.product.StockQuantityRequest;
import com.taytelar.response.SuccessResponse;
import com.taytelar.response.product.*;
import com.taytelar.service.service.product.ProductService;
import com.taytelar.util.Constants;
import com.taytelar.util.Generator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import static java.util.Objects.isNull;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductServiceImplementation implements ProductService {

    private final CategoryRepository categoryRepository;
    private final SubCategoryRepository subCategoryRepository;
    private final ProductRepository productRepository;
    private final S3Client s3Client;
    private final Generator generator;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${aws.bucketName}")
    private String bucketName;

    @Transactional
    @Override
    public AddProductResponse addProduct(AddProductRequest request) {
        log.info("Add product request : {}", request);

        Category category = getOrCreateCategory(request.getCategoryName(), request.getCategoryDescription());
        SubCategory subCategory = getOrCreateSubCategory(request.getSubCategoryName(), request.getSubCategoryDescription(), category);

        Product product = createProduct(request, subCategory);
        log.info("Product : {}", product);
        productRepository.save(product);

        return new AddProductResponse(product.getProductId(), Constants.PRODUCT_ADDED_SUCCESSFULLY, HttpStatus.OK.value());
    }

    @Override
    public SuccessResponse uploadProductFiles(String productId, MultipartFile[] images, Integer[] imagePriorities, MultipartFile video) {
        Product product = productRepository.findByProductId(productId);
        if (isNull(product)) {
            throw new ProductNotFoundException(Constants.PRODUCT_NOT_FOUND);
        }
        Map<String, Integer> imageUrls = uploadFilesToS3(imagePriorities, images, productId);
        String videoUrl = uploadFileToS3(video, "video", productId);
        log.info("Image URLs: {} and Video URL: {}", imageUrls, videoUrl);

        product.setImageUrls(imageUrls);
        product.setVideoUrl(videoUrl);

        productRepository.save(product);
        return new SuccessResponse(Constants.IMAGES_AND_VIDEO_ADDED_SUCCESSFULLY, HttpStatus.OK.value());
    }

    @Override
    public ProductResponse getAllProducts() {
        List<Category> categories = categoryRepository.findAll();
        log.info("Category Data : {}", categories);

        List<CategoryResponse> categoryResponses = categories.stream()
                .map(this::convertCategoryToResponse)
                .toList();

        log.info("Category Data Response : {}", categoryResponses);
        return new ProductResponse(categoryResponses);
    }


    private Category getOrCreateCategory(String categoryName, String categoryDescription) {
        Optional<Category> optionalCategory = categoryRepository.findByCategoryName(categoryName);

        if (optionalCategory.isPresent()) {
            Category category = optionalCategory.get();
            log.info("Category already exists: {}", category);
            return category;
        } else {
            Category newCategory = new Category();
            newCategory.setCategoryId(generator.generateId(Constants.CATEGORY_ID));
            newCategory.setCategoryName(categoryName);
            newCategory.setCategoryDescription(categoryDescription);
            categoryRepository.save(newCategory);
            log.info("Created new Category: {}", newCategory);
            return newCategory;
        }
    }

    private SubCategory getOrCreateSubCategory(String subCategoryName, String subCategoryDescription, Category category) {
        Optional<SubCategory> optionalSubCategory = subCategoryRepository.findBySubCategoryName(subCategoryName);
        if (optionalSubCategory.isPresent()) {
            SubCategory subCategory = optionalSubCategory.get();
            log.info("SubCategory already exist: {}", subCategory);
            return subCategory;

        } else {
            SubCategory newSubCategory = new SubCategory();
            newSubCategory.setSubCategoryId(generator.generateId(Constants.SUB_CATEGORY_ID));
            newSubCategory.setSubCategoryName(subCategoryName);
            newSubCategory.setSubCategoryDescription(subCategoryDescription);
            newSubCategory.setCategory(category);
            subCategoryRepository.save(newSubCategory);
            log.info("SubCategory: {}", newSubCategory);
            return newSubCategory;
        }
    }

    private Product createProduct(AddProductRequest request, SubCategory subCategory) {
        log.info("Product Request :{}", request);
        Product product = new Product();
        product.setProductId(generator.generateId(Constants.PRODUCT_ID));
        product.setProductName(request.getProductName());
        product.setProductStatus(request.getProductStatus());
        product.setProductDescription(request.getProductDescription());
        product.setProductMaterialType(request.getProductMaterialType());
        product.setProductPattern(request.getProductPattern());
        product.setProductPrice(request.getProductPrice());
        product.setStockQuantities(mapStockQuantities(request.getStockQuantities(), product));
        product.setSubCategory(subCategory);
        return product;
    }

    private List<StockQuantity> mapStockQuantities(List<StockQuantityRequest> stockQuantities, Product product) {
        List<StockQuantity> result = new ArrayList<>();
        for (StockQuantityRequest stockQuantity : stockQuantities) {
            StockQuantity quantity = new StockQuantity();
            quantity.setStockId(generator.generateId(Constants.STOCK_ID));
            quantity.setSize(stockQuantity.getSize());
            quantity.setProduct(product);
            quantity.setColorQuantities(mapColorQuantities(stockQuantity.getColorQuantities(), quantity));
            result.add(quantity);
        }
        return result;
    }

    private List<ColorQuantity> mapColorQuantities(List<ColorQuantityRequest> colorQuantities, StockQuantity quantity) {
        List<ColorQuantity> result = new ArrayList<>();
        for (ColorQuantityRequest colorQuantity : colorQuantities) {
            ColorQuantity quantity1 = new ColorQuantity();
            quantity1.setColorQuantityId(generator.generateId(Constants.COLOR_QUANTITY_ID));
            quantity1.setColor(colorQuantity.getColor());
            quantity1.setColorCode(colorQuantity.getColorCode());
            quantity1.setQuantity(colorQuantity.getQuantity());
            quantity1.setStockQuantity(quantity);
            result.add(quantity1);
        }
        return result;
    }

    private Map<String, Integer> uploadFilesToS3(Integer[] imagePriorities, MultipartFile[] imageFiles, String productId) {
        Map<String, Integer> fileUrls = new HashMap<>();

        for (int i = 0; i < imageFiles.length; i++) {
            MultipartFile file = imageFiles[i];
            Integer priority = imagePriorities[i];

            String fileUrl = uploadFileToS3(file, "images", productId);
            fileUrls.put(fileUrl, priority);
        }
        log.info("Images URL: {}",fileUrls);
        return fileUrls;
    }


    private String uploadFileToS3(MultipartFile file, String fileType, String productId) {
        String slash = "/";
        String fileName = productId + slash + fileType + slash + file.getOriginalFilename();
        try (InputStream inputStream = file.getInputStream()) {
            s3Client.putObject(PutObjectRequest.builder()
                            .bucket(bucketName)
                            .key(fileName)
                            .build(),
                    RequestBody.fromInputStream(inputStream, file.getSize()));

            return s3Client.utilities().getUrl(GetUrlRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .build()).toExternalForm();
        } catch (IOException e) {
            log.error("Error uploading file to S3: {}", e.getMessage(), e);
            throw new S3UploadException(Constants.UPLOAD_ERROR);
        }
    }

    private CategoryResponse convertCategoryToResponse(Category category) {
        List<SubCategory> subCategories = subCategoryRepository.findByCategory(category);
        log.info("SubCategory Data : {}", subCategories);

        List<SubCategoryResponse> subCategoryResponses = subCategories.stream()
                .map(this::convertSubCategoryToResponse)
                .toList();

        log.info("SubCategory Data Response : {}", subCategoryResponses);
        return new CategoryResponse(
                category.getCategoryId(),
                category.getCategoryName(),
                category.getCategoryDescription(),
                subCategoryResponses
        );
    }

    private SubCategoryResponse convertSubCategoryToResponse(SubCategory subCategory) {
        List<Product> products = productRepository.findBySubCategory(subCategory);
        log.info("Product Data : {}", products);

        List<ProductDataResponse> productDataResponses = products.stream()
                .map(this::convertProductToResponse)
                .toList();

        log.info("Product Data Response : {}", productDataResponses);
        return new SubCategoryResponse(
                subCategory.getSubCategoryId(),
                subCategory.getSubCategoryName(),
                subCategory.getSubCategoryDescription(),
                productDataResponses
        );
    }

    private ProductDataResponse convertProductToResponse(Product product) {
        return new ProductDataResponse(
                product.getProductId(),
                product.getProductName(),
                product.getProductStatus(),
                product.getProductDescription(),
                product.getProductMaterialType(),
                product.getProductPattern(),
                product.getProductPrice(),
                product.getStockQuantities().stream()
                        .map(stock -> new StockQuantityResponse(
                                stock.getSize(),
                                stock.getColorQuantities()
                                        .stream()
                                        .map(colorQuantity -> new ColorQuantityResponse(
                                                colorQuantity.getColor(),
                                                colorQuantity.getColorCode(),
                                                colorQuantity.getQuantity()
                                        ))
                                        .toList()
                        ))
                        .toList(),
                product.getImageUrls(),
                product.getVideoUrl()
        );
    }
}
