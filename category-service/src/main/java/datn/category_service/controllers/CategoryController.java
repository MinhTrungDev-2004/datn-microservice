package datn.category_service.controllers;

import datn.category_service.models.dtos.category.CategoryCreateRequest;
import datn.category_service.models.global.ApiResult;
import datn.category_service.services.Interfaces.ICategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/category")
public class CategoryController extends ApiBaseController {
    private final ICategoryService categoryService;

    public CategoryController(ICategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResult<UUID>> createCategory(@RequestBody CategoryCreateRequest request) {
        return exeResponseEntity(() -> categoryService.createCategory(request));
    }
}
