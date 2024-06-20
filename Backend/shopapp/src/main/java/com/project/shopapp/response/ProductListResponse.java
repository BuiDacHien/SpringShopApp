package com.project.shopapp.response;

import com.project.shopapp.entity.Product;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductListResponse {
    private int totalPages;
    private List<ProductResponse> products;
}
