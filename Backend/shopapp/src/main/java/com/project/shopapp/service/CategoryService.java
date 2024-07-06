package com.project.shopapp.service;

import com.project.shopapp.dto.CategoryDto;
import com.project.shopapp.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

public interface CategoryService {
    Category createCategory(CategoryDto categoryDto);
    Category getCategoryById(Long id);

    List<Category> getAllCategories();

    Category updateCategory(CategoryDto categoryDto, Long id);

    void deleteCategory(Long id);
}
