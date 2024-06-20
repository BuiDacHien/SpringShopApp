package com.project.shopapp.service.Impl;

import com.project.shopapp.dto.CategoryDto;
import com.project.shopapp.entity.Category;
import com.project.shopapp.exception.ResourceNotFoundException;
import com.project.shopapp.repository.CategoryRepository;
import com.project.shopapp.service.CategoryService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public Category createCategory(CategoryDto categoryDto) {
        Category newCategory = Category.builder().name(categoryDto.getName()).build();
        return categoryRepository.save(newCategory);
    }

    @Override
    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public Category updateCategory(CategoryDto categoryDto, Long id) {
        Category existCategory = getCategoryById(id);
        existCategory.setName(categoryDto.getName());
        categoryRepository.save(existCategory);
        return existCategory;
    }

    @Override
    public void deleteCategory(Long id) {
        Category existCategory = getCategoryById(id);
        categoryRepository.deleteById(id);
    }
}
