package datn.category_service.services.Interfaces;

import datn.category_service.models.dtos.category.CategoryCreateRequest;
import datn.category_service.models.global.ApiResult;

import java.util.UUID;

public interface ICategoryService {
    ApiResult<UUID> createCategory(CategoryCreateRequest request);
}
