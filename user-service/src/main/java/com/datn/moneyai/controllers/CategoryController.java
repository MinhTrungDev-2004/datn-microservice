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

    /**
     * API tạo mới một danh mục (Category).
     *
     * @param request Dữ liệu đầu vào chứa thông tin danh mục cần tạo.
     * @return ResponseEntity chứa ApiResult mang theo đối tượng CategoryResponse
     *         vừa tạo.
     */
    @PostMapping("/create")
    public ResponseEntity<ApiResult<CategoryResponse>> createCategory(@RequestBody CategoryRequest request) {
        return exeResponseEntity(() -> categoryService.createCategory(request));
    }

    /**
     * API cập nhật thông tin một danh mục (Category) theo ID.
     *
     * @param id      ID của danh mục cần cập nhật.
     * @param request Dữ liệu đầu vào chứa thông tin danh mục cần cập nhật.
     * @return ResponseEntity chứa ApiResult mang theo đối tượng CategoryResponse
     *         vừa được cập nhật.
     */
    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResult<CategoryResponse>> updateCategory(@PathVariable Long id,
            @RequestBody CategoryRequest request) {
        return exeResponseEntity(() -> categoryService.updateCategory(id, request));
    }

    /**
     * API lấy danh sách tất cả danh mục (Category) của người dùng hiện tại.
     *
     * @return ResponseEntity chứa ApiResult mang theo danh sách đối tượng
     *         CategoryResponse.
     */
    @GetMapping("/gets-all")
    public ResponseEntity<ApiResult<List<CategoryResponse>>> getAllCategory() {
        return exeResponseEntity(() -> categoryService.getsCategory());
    }

    /**
     * API xóa một danh mục (Category) theo ID.
     *
     * @param id ID của danh mục cần xóa.
     * @return ResponseEntity chứa ApiResult mang theo null nếu xóa thành công.
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResult<Void>> deleteCategory(@PathVariable Long id) {
        return exeResponseEntity(() -> categoryService.deleteCategory(id));
    }
}
