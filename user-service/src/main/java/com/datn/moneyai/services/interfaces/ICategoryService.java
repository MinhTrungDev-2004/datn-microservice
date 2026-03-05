package com.datn.moneyai.services.interfaces;

import java.util.Optional;

import com.datn.moneyai.models.dtos.categories.CategoryCreateRequest;
import com.datn.moneyai.models.entities.bases.CategoryEntity;

public interface ICategoryService {
    CategoryEntity createCategory(CategoryCreateRequest request);

    void updateCategory(Long id, CategoryCreateRequest request);
}
