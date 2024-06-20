package com.project.shopapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.shopapp.entity.Product;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Min;
import lombok.*;


@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductImageDto {

    @JsonProperty("image_url")
    private String imageUrl;

    @JsonProperty("product_id")
    @Min(value = 1, message = "Product 's ID must be >= 1")
    private Long productId;
}
