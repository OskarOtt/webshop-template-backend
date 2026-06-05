package com.oskarott.webshoptemplatebackend.service;

import com.oskarott.webshoptemplatebackend.dto.CategoryRequest;
import com.oskarott.webshoptemplatebackend.dto.CategoryResponse;
import com.oskarott.webshoptemplatebackend.model.Category;
import com.oskarott.webshoptemplatebackend.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<CategoryResponse> getRoots() {
        return categoryRepository.findByParentIsNull().stream()
                .map(CategoryResponse::from)
                .toList();
    }

    public CategoryResponse getById(Long id) {
        return CategoryResponse.from(findOrThrow(id));
    }

    public List<CategoryResponse> getChildren(Long parentId) {
        findOrThrow(parentId);
        return categoryRepository.findByParentId(parentId).stream()
                .map(CategoryResponse::from)
                .toList();
    }

    public CategoryResponse create(CategoryRequest request) {
        Category category = new Category();
        applyRequest(category, request);
        return CategoryResponse.from(categoryRepository.save(category));
    }

    public CategoryResponse update(Long id, CategoryRequest request) {
        Category category = findOrThrow(id);
        applyRequest(category, request);
        return CategoryResponse.from(categoryRepository.save(category));
    }

    public void delete(Long id) {
        findOrThrow(id);
        categoryRepository.deleteById(id);
    }

    public Category findOrThrow(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Category not found: " + id));
    }

    private void applyRequest(Category category, CategoryRequest request) {
        category.setName(request.name());
        if (request.parentId() != null) {
            category.setParent(findOrThrow(request.parentId()));
        } else {
            category.setParent(null);
        }
    }
}
