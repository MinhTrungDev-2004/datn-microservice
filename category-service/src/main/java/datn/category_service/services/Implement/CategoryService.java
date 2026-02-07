package datn.category_service.services.Implement;

import datn.category_service.models.dtos.category.CategoryCreateRequest;
import datn.category_service.models.entities.bases.CategoryEntity;
import datn.category_service.models.entities.enums.TypeCategory;
import datn.category_service.models.global.ApiResult;
import datn.category_service.repositories.CategoryRepository;
import datn.category_service.services.Interfaces.ICategoryService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CategoryService implements ICategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public ApiResult<UUID> createCategory(CategoryCreateRequest request) {
        CategoryEntity newCategory = CategoryEntity.builder()
                .title(request.getTitle())
                .icon(request.getIcon())
                .color(request.getColor())
                .type(TypeCategory.valueOf(request.getType().toUpperCase()))
                .build();
        categoryRepository.save(newCategory);
        return ApiResult.success(newCategory.getId(), "Tạo danh mục thành công");
    }
}
