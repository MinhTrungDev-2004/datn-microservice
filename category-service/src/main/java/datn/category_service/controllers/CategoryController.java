package datn.category_service.controllers;

import datn.category_service.models.dtos.category.CategoryCreateRequest;
import datn.category_service.models.entities.bases.CategoryEntity;
import datn.category_service.models.global.ApiResult;
import datn.category_service.services.Interfaces.ICategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/categories")
public class CategoryController extends ApiBaseController {
    private final ICategoryService categoryService;

    public CategoryController(ICategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResult<String>> createCategory(@RequestBody CategoryCreateRequest request) {
        return ResponseEntity.ok (
                ApiResult.success(categoryService.createCategory(request), "Tạo danh mục thành công"));
    }

    @PutMapping("/update")
    public ResponseEntity<ApiResult<CategoryEntity>> updateCategory(@RequestBody CategoryCreateRequest request) {
        return exeResponseEntity(() -> {
            Optional<CategoryEntity> optionalCategory = categoryService.updateCategory(request);
            if (optionalCategory.isEmpty()) {
                throw new RuntimeException("Không tìm thấy danh mục để cập nhật!");
            }
            return ApiResult.success(optionalCategory.get(), "Cập nhật thành công");
        });
    }
}
