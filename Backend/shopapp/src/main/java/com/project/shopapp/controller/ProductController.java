package com.project.shopapp.controller;

import com.project.shopapp.components.LocalizationUtils;
import com.project.shopapp.dto.ProductDto;
import com.project.shopapp.dto.ProductImageDto;
import com.project.shopapp.entity.Product;
import com.project.shopapp.entity.ProductImage;
import com.project.shopapp.response.ProductListResponse;
import com.project.shopapp.response.ProductResponse;
import com.project.shopapp.service.ProductRedisService;
import com.project.shopapp.service.ProductService;
import com.project.shopapp.utils.CommonStrings;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("${api.base.path}/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final LocalizationUtils localizationUtils;
    private final ProductRedisService productRedisService;
    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    @PostMapping(value = "")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> createProduct(@Valid @RequestBody ProductDto productDto,
                                           BindingResult result) {
        try {
            if (result.hasErrors()) {
                List<String> errorMessages = result.getFieldErrors()
                        .stream().map(FieldError::getDefaultMessage)
                        .toList();
                return ResponseEntity.badRequest().body(errorMessages);
            }

            Product newProduct = productService.createProduct(productDto);
            return ResponseEntity.ok(newProduct);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping(value = "/upload_images/{productId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> uploadProductImage(@PathVariable Long productId, @ModelAttribute("files") List<MultipartFile> files) {

        try {
            Product productExist = productService.getProductById(productId);

            files = files == null ? new ArrayList<MultipartFile>() : files;
            if (files.size() > ProductImage.MAXIMUM_IMAGES_PER_PRODUCT) {
                return ResponseEntity.badRequest().body(localizationUtils
                        .getLocalizedMessage(CommonStrings.UPLOAD_IMAGES_MAX_5));
            }

            List<ProductImage> productImages = new ArrayList<>();

            for (MultipartFile file : files) {

                // When no file upload or file size = 0 mb
                if (file.getSize() == 0) {
                    continue;
                }

                // file size > 10MB
                if (file.getSize() > 10 * 1024 * 1024) {
                    return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(localizationUtils
                            .getLocalizedMessage(CommonStrings.UPLOAD_IMAGES_FILE_LARGE));
                }

                String contentType = file.getContentType();
                if (contentType == null || !contentType.startsWith("image/")) {
                    return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(localizationUtils.getLocalizedMessage(CommonStrings.UPLOAD_IMAGES_FILE_MUST_BE_IMAGE));
                }

                String fileName = storeFile(file);
                // Save file images to product_images
                ProductImage newProductImage = productService.createProductImage(productExist.getId(), ProductImageDto.builder()
                        .imageUrl(fileName)
                        .build());

                productImages.add(newProductImage);
            }

            return ResponseEntity.ok().body(productImages);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/images/{imageName}")
    public ResponseEntity<?> viewImage(@PathVariable String imageName) {
        try {
            java.nio.file.Path imagePath = Paths.get("uploads/" + imageName);
            UrlResource resource = new UrlResource(imagePath.toUri());

            if (resource.exists()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(resource);
            } else {
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(new UrlResource(Paths.get("uploads/notfound.jpg").toUri()));
                //return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }


    private String storeFile(MultipartFile file) throws IOException {
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));

        // Add uuid to ensure the file name is unique to avoid overwriting
        String newFileName = UUID.randomUUID().toString() + "_" + fileName;

        // Path to save file
        Path path = Paths.get("uploads");

        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }

        Path destination = Paths.get(path.toString(), newFileName);

        // Copy file to destination folder
        Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
        return newFileName;
    }

    @GetMapping("")
    public ResponseEntity<?> getAllProducts(
            @RequestParam(defaultValue = "", name = "keyword") String keyword,
            @RequestParam(defaultValue = "0", name = "category_id") Long categoryId,
            @RequestParam(defaultValue = "0", name = "page") int page,
            @RequestParam(defaultValue = "10", name = "limit") int limit
    ) {
        try {
            PageRequest pageRequest = PageRequest.of(page, limit, Sort.by("id").ascending());

            int totalPages = 0;

            logger.info(String.format("keyword = %s, category_id = %d, page = %d, limit = %d",
                    keyword, categoryId, page, limit));

            List<ProductResponse> productResponses = productRedisService
                    .getAllProducts(keyword, categoryId, pageRequest);
            if(productResponses == null) {
                Page<ProductResponse> productPage = productService
                        .getAllProducts(keyword, categoryId, pageRequest);
                // Lấy tổng số trang
                totalPages = productPage.getTotalPages();
                productResponses = productPage.getContent();
                productRedisService.saveAllProducts(
                        productResponses,
                        keyword,
                        categoryId,
                        pageRequest
                );
            }

            return ResponseEntity.ok(ProductListResponse
                    .builder()
                    .products(productResponses)
                    .totalPages(totalPages)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable Long id) {
        try {
            Product product = productService.getProductById(id);

            ProductResponse productResponse = ProductResponse.mapFromProduct(product);
            return ResponseEntity.ok().body(productResponse);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/by-ids")
    public ResponseEntity<?> getProductsByIds(@RequestParam("ids") String ids) {
        //eg: 1,3,5,7
        try {
            // Tách chuỗi ids thành một mảng các số nguyên
            List<Long> productIds = Arrays.stream(ids.split(","))
                    .map(Long::parseLong)
                    .collect(Collectors.toList());
            List<Product> products = productService.findProductsByIds(productIds);
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(security = { @SecurityRequirement(name = "bearer-key") })
    public ResponseEntity<?> updateProduct(@PathVariable Long id, @RequestBody ProductDto productDto) {
        try {
            Product updateProduct = productService.updateProduct(id, productDto);

            return ResponseEntity.ok().body(ProductResponse.mapFromProduct(updateProduct));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(security = { @SecurityRequirement(name = "bearer-key") })
    public ResponseEntity<String> deleteProduct(@PathVariable Long id) {
        try {
            productService.deleteProduct(id);
        } catch (Exception e) {
            ResponseEntity.badRequest().body(e.getMessage());
        }

        return ResponseEntity.ok("Delete product by id = " + id);
    }

//    @PostMapping("/genData")
//    public ResponseEntity<String> genDataProducts() {
//        Faker faker = new Faker();
//        for (int i = 0; i < 10000; i++) {
//
//            // If product name exist -> continue
//            String productName = faker.commerce().productName();
//            if (productService.existsByName(productName)) {
//                continue;
//            }
//
//            ProductDto productDto = ProductDto.builder()
//                    .name(productName)
//                    .price((float) faker.number().numberBetween(10, 50000000))
//                    .description(faker.lorem().sentence())
//                    .thumbnail("")
//                    .categoryId((long) faker.number().numberBetween(1, 4))
//                    .build();
//
//            try {
//                productService.createProduct(productDto);
//            } catch (Exception e) {
//                ResponseEntity.badRequest().body(e.getMessage());
//            }
//        }
//
//        return ResponseEntity.ok("Fake 1 millions product records successfully!");
//    }
}
