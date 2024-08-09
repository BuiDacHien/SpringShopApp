package com.project.shopapp.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.project.shopapp.response.ProductResponse;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public interface ProductRedisService {

    // Clear cached data in Redis
    void clear();//clear cache
    List<ProductResponse> getAllProducts(
            String keyword,
            Long categoryId, PageRequest pageRequest) throws JsonProcessingException;
    void saveAllProducts(List<ProductResponse> productResponses,
                         String keyword,
                         Long categoryId,
                         PageRequest pageRequest) throws JsonProcessingException;
}
