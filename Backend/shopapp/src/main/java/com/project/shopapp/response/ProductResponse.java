package com.project.shopapp.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.shopapp.entity.Product;
import com.project.shopapp.entity.ProductImage;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductResponse extends BaseResponse {
    private Long id;
    private String name;
    private Float price;
    private String thumbnail;
    private String description;

    @JsonProperty("category_id")
    private Long categoryId;

    @JsonProperty("product_images")
    private List<ProductImage> productImages = new ArrayList<>();


    public static ProductResponse mapFromProduct(Product product) {
        ProductResponse productResponse = ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .thumbnail(product.getThumbnail())
                .description(product.getDescription())
                .categoryId(product.getCategory().getId())
                .productImages(product.getProductImages())
                .build();
        productResponse.setCreatedAt(product.getCreatedAt());
        productResponse.setUpdatedAt(product.getUpdatedAt());
        return productResponse;
    }
}
