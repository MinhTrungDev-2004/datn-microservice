package com.datn.moneyai.controllers;

import com.datn.moneyai.models.entities.bases.CategoryEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.datn.moneyai.models.dtos.categories.CategoryCreateRequest;
import com.datn.moneyai.models.global.ApiResult;
import com.datn.moneyai.services.interfaces.ICategoryService;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/category")
public class CategoryController extends ApiBaseController {
    private final ICategoryService categoryService;

    public CategoryController(ICategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResult<CategoryEntity>> createCategory(@RequestBody CategoryCreateRequest request) {
        return ResponseEntity.ok(
                ApiResult.success(categoryService.createCategory(request), "Tạo danh mục thành công"));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateCategory(@PathVariable Long id, @RequestBody CategoryCreateRequest request) {
        categoryService.updateCategory(id, request);
        return ResponseEntity.ok(ApiResult.success(null, "Cập nhật danh mục thành công"));
    }
}