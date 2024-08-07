package com.project.shopapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductDto {
    @NotBlank(message = "Product 's name can't empty !")
    @Size(min = 5, max = 300, message = "Product name must be between 5 and 300 characters")
    private String name;

    @Min(value = 0, message = "Price must be greater than or equal to 0")
    @Max(value = 50000000, message = "Price must be less than or equal to 50000000")
    private Float price;

    private String thumbnail;
    private String description;

    @JsonProperty("category_id")
    private Long categoryId;

//    private List<MultipartFile> files;
}
