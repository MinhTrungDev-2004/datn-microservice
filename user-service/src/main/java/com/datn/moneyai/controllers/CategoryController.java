package com.datn.moneyai.controllers;

import com.datn.moneyai.models.dtos.category.CategoryRequest;
import com.datn.moneyai.models.dtos.category.CategoryResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.datn.moneyai.models.global.ApiResult;
import com.datn.moneyai.services.interfaces.ICategoryService;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/category")
public class CategoryController extends ApiBaseController {
    private final ICategoryService categoryService;

    /*
     * Hàm Khởi tạo Constructor
     */
    public CategoryController(ICategoryService categoryService) {
        this.categoryService = categoryService;
    }

    /*
     * API tạo mới danh mục category
     */
    @PostMapping("/create")
    public ResponseEntity<ApiResult<CategoryResponse>> createCategory(@RequestBody CategoryRequest request) {
        CategoryResponse data = categoryService.createCategory(request);
        return ResponseEntity.ok(ApiResult.success(data, "Tạo danh mục thành công"));
    }

    /*
     * API chỉ sửa category by id
     */
    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResult<CategoryResponse>> updateCategory(@PathVariable Long id,
            @RequestBody CategoryRequest request) {
        CategoryResponse data = categoryService.updateCategory(id, request);
        return ResponseEntity.ok(ApiResult.success(data, "Cập nhật danh mục thành công"));
    }
}
