package com.datn.moneyai.controllers;

import com.datn.moneyai.models.dtos.category.CategoryRequest;
import com.datn.moneyai.models.dtos.category.CategoryResponse;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.datn.moneyai.models.global.ApiResult;
import com.datn.moneyai.services.interfaces.ICategoryService;

import lombok.AllArgsConstructor;

import org.springframework.web.bind.annotation.RequestBody;

@RestController
@AllArgsConstructor
@RequestMapping("/category")
public class CategoryController extends ApiBaseController {
    private final ICategoryService categoryService;

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

    /*
     * API lấy danh sách category
     */
        @GetMapping("/gets-all")
    public ResponseEntity<ApiResult<List<CategoryResponse>>> getAllCategory() {
        List<CategoryResponse> data = categoryService.getsCategory();
        return ResponseEntity.ok(ApiResult.success(data, "Lấy danh sách danh mục thành công"));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResult<Void>> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok(ApiResult.success(null, "Xóa danh mục thành công"));
    }
}
