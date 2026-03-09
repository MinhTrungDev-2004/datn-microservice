package com.datn.moneyai.services.interfaces;

import java.util.List;
import com.datn.moneyai.models.dtos.category.CategoryRequest;
import com.datn.moneyai.models.dtos.category.CategoryResponse;
import com.datn.moneyai.models.global.ApiResult;

public interface ICategoryService {
    ApiResult<CategoryResponse> createCategory(CategoryRequest request);

    ApiResult<CategoryResponse> updateCategory(Long id, CategoryRequest request);

    ApiResult<List<CategoryResponse>> getsCategory();

    ApiResult<Void> deleteCategory(Long id);
}
