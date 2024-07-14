package com.project.shopapp.controller;

import com.github.javafaker.Faker;
import com.project.shopapp.components.LocalizationUtils;
import com.project.shopapp.dto.ProductDto;
import com.project.shopapp.dto.ProductImageDto;
import com.project.shopapp.entity.Product;
import com.project.shopapp.entity.ProductImage;
import com.project.shopapp.response.ProductListResponse;
import com.project.shopapp.response.ProductResponse;
import com.project.shopapp.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("${api.base.path}/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final LocalizationUtils localizationUtils;

    @PostMapping(value = "")
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
    public ResponseEntity<?> uploadProductImage(@PathVariable Long productId, @ModelAttribute("files") List<MultipartFile> files) {

        try {
            Product productExist = productService.getProductById(productId);

            List<ProductImage> productImages = new ArrayList<>();

            if (files.size() > ProductImage.MAXIMUM_IMAGES_PER_PRODUCT) {
                return ResponseEntity.badRequest().body("You can only upload a maximum of 5 images");
            }
            for (MultipartFile file : files) {

                // When no file upload or file size = 0 mb
                if (file.getSize() == 0) {
                    continue;
                }

                // file size > 10MB
                if (file.getSize() > 10 * 1024 * 1024) {
                    return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body("The file is too large. Maximum is 10 MB");
                }

                String contentType = file.getContentType();
                if (contentType == null || !contentType.startsWith("image/")) {
                    return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body("File must be an image!");
                }

                String fileName = storeFile(file);
                // Save file images to product_images
                ProductImage newProductImage = productService.createProductImage(productId, new ProductImageDto(fileName, productId));

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
            java.nio.file.Path imagePath = Paths.get("uploads/"+imageName);
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
    public ResponseEntity<?> getAllProducts(@RequestParam("page") int page, @RequestParam("limit") int limit) {
        try {
            PageRequest pageRequest = PageRequest.of(page, limit, Sort.by("createdAt").descending());

            Page<ProductResponse> productPage = productService.getAllProducts(pageRequest);

            int totalPages = productPage.getTotalPages();
            List<ProductResponse> products = productPage.getContent();


            ProductListResponse productListResponse = ProductListResponse.builder()
                    .products(products)
                    .totalPages(totalPages)
                    .build();

            return ResponseEntity.ok().body(productListResponse);
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

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable Long id, @RequestBody ProductDto productDto) {
        try {
            Product updateProduct = productService.updateProduct(id, productDto);

            return ResponseEntity.ok().body(ProductResponse.mapFromProduct(updateProduct));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long id) {
        try {
            productService.deleteProduct(id);
        } catch (Exception e) {
            ResponseEntity.badRequest().body(e.getMessage());
        }

        return ResponseEntity.ok("Delete product by id = " + id);
    }

    @PostMapping("/genData")
    public ResponseEntity<String> genDataProducts() {
        Faker faker = new Faker();
        for (int i = 0; i < 10000; i++) {

            // If product name exist -> continue
            String productName = faker.commerce().productName();
            if (productService.existsByName(productName)) {
                continue;
            }

            ProductDto productDto = ProductDto.builder()
                    .name(productName)
                    .price((float) faker.number().numberBetween(10, 50000000))
                    .description(faker.lorem().sentence())
                    .thumbnail("")
                    .categoryId((long) faker.number().numberBetween(1, 4))
                    .build();

            try {
                productService.createProduct(productDto);
            } catch (Exception e) {
                ResponseEntity.badRequest().body(e.getMessage());
            }
        }

        return ResponseEntity.ok("Fake 1 millions product records successfully!");
    }
}
