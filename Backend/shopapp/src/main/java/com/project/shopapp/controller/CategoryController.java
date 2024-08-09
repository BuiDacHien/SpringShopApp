package com.project.shopapp.controller;

import com.project.shopapp.components.LocalizationUtils;
import com.project.shopapp.dto.CategoryDto;
import com.project.shopapp.entity.Category;
import com.project.shopapp.response.CategoryResponse;
import com.project.shopapp.response.UpdateCategoryResponse;
import com.project.shopapp.service.CategoryService;
import com.project.shopapp.utils.CommonStrings;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("${api.base.path}/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;
    private final LocalizationUtils localizationUtils;


    @PostMapping("")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> createCategory(@Valid @RequestBody CategoryDto categoryDto, BindingResult result) {
        try {
            CategoryResponse categoryResponse = new CategoryResponse();
            if(result.hasErrors()) {
                List<String> errorMessages = result.getFieldErrors()
                        .stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();
                categoryResponse.setMessage(localizationUtils.getLocalizedMessage(CommonStrings.INSERT_CATEGORY_FAILED));
                categoryResponse.setErrors(errorMessages);
                return ResponseEntity.badRequest().body(categoryResponse);
            }
            Category category = categoryService.createCategory(categoryDto);
            categoryResponse.setCategory(category);
            return ResponseEntity.ok(categoryResponse);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    @GetMapping("")
    public ResponseEntity<List<Category>> getAllCategories(@RequestParam("page") int page, @RequestParam("limit") int limit) {
        List<Category> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> updateCategory(@PathVariable Long id, @Valid @RequestBody CategoryDto categoryDto, BindingResult result) {
        try {
            if (result.hasErrors()) {
                List<String> errorMessages = result.getFieldErrors()
                        .stream().map(FieldError::getDefaultMessage)
                        .toList();
                return ResponseEntity.badRequest().body(errorMessages);
            }
            UpdateCategoryResponse updateCategoryResponse = new UpdateCategoryResponse();
            categoryService.updateCategory(categoryDto, id);
            updateCategoryResponse.setMessage(localizationUtils.getLocalizedMessage(CommonStrings.UPDATE_CATEGORY_SUCCESSFULLY));
            return ResponseEntity.ok(updateCategoryResponse);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok(localizationUtils.getLocalizedMessage(CommonStrings.DELETE_CATEGORY_SUCCESSFULLY));
    }
}
