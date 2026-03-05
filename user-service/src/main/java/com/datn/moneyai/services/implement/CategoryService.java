package com.datn.moneyai.services.implement;

import com.datn.moneyai.exceptions.UserMessageException;
import com.datn.moneyai.models.dtos.category.CategoryRequest;
import com.datn.moneyai.models.dtos.category.CategoryResponse;
import com.datn.moneyai.models.entities.bases.CategoryEntity;
import com.datn.moneyai.models.entities.bases.User;
import com.datn.moneyai.models.entities.enums.CategoryType;
import com.datn.moneyai.repositories.CategoryRepository;
import com.datn.moneyai.repositories.UserRepository;
import com.datn.moneyai.services.interfaces.ICategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryService implements ICategoryService {
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    @Override
    public CategoryResponse createCategory(CategoryRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserMessageException("Không tìm thấy người dùng."));

        if (request.getType() == null) {
            throw new UserMessageException("Loại danh mục không được để trống.");
        }

        CategoryType type;
        try {
            type = request.getType();
        } catch (IllegalArgumentException e) {
            throw new UserMessageException("Loại danh mục không hợp lệ (Chỉ nhận CHI hoặc THU).");
        }

        CategoryEntity newCategory = CategoryEntity.builder()
                .name(request.getName())
                .icon(request.getIcon())
                .colorCode(request.getColorCode())
                .type(request.getType())
                .user(user)
                .build();
        CategoryEntity saved = categoryRepository.save(newCategory);
        return CategoryResponse.builder()
                .id(saved.getId())
                .name(saved.getName())
                .type(saved.getType())
                .icon(saved.getIcon())
                .colorCode(saved.getColorCode())
                .createdAt(saved.getCreatedAt())
                .updatedAt(saved.getUpdatedAt())
                .build();
    }

    @Override
    public CategoryResponse updateCategory(Long categoryId, CategoryRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserMessageException("Không tìm thấy người dùng."));

        CategoryEntity category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new UserMessageException("Không tìm thấy danh mục để cập nhật!"));

        if (!category.getUser().getId().equals(user.getId())) {
            throw new UserMessageException("Bạn không có quyền cập nhật danh mục này.");
        }

        if (request.getName() != null) {
            category.setName(request.getName());
        }
        if (request.getIcon() != null) {
            category.setIcon(request.getIcon());
        }
        if (request.getColorCode() != null) {
            category.setColorCode(request.getColorCode());
        }
        if (request.getType() != null) {
            CategoryType type = request.getType();
            category.setType(type);
        }

        CategoryEntity saved = categoryRepository.save(category);
        return CategoryResponse.builder()
                .id(saved.getId())
                .name(saved.getName())
                .type(saved.getType())
                .icon(saved.getIcon())
                .colorCode(saved.getColorCode())
                .createdAt(saved.getCreatedAt())
                .updatedAt(saved.getUpdatedAt())
                .build();
    }
}
