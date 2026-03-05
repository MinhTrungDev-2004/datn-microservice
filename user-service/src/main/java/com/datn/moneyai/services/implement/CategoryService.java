package com.datn.moneyai.services.implement;

import com.datn.moneyai.exceptions.UserMessageException;
import com.datn.moneyai.models.dtos.categories.CategoryCreateRequest;
import com.datn.moneyai.models.entities.bases.CategoryEntity;
import com.datn.moneyai.models.entities.bases.User;
import com.datn.moneyai.models.entities.enums.CategoryType;
import com.datn.moneyai.repositories.CategoryRepository;
import com.datn.moneyai.repositories.UserRepository;
import com.datn.moneyai.services.interfaces.ICategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryService implements ICategoryService {
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    @Override
    public CategoryEntity createCategory(CategoryCreateRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserMessageException("Không tìm thấy người dùng."));

        if (request.getType() == null ) {
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

        // TẠO MỚI: Không cần ID, JPA sẽ tự sinh ra và lưu vào database
        return categoryRepository.save(newCategory);
    }

    @Override
    public void updateCategory(Long categoryId, CategoryCreateRequest request) {
//        CategoryEntity categoryEntity = categoryRepository.findCategoryById(categoryId)
//                .orElseThrow(() -> new UserMessageException("Không tìm thấy danh mục để cập nhật!"));
//        if (categoryEntity != null) {
//            categoryEntity.setName(request.getName());
//            categoryEntity.setIcon(request.getIcon());
//            categoryEntity.setColorCode(request.getColorCode());
//            if (request.getType() != null && !request.getType().trim().isEmpty()) {
//                try {
//                    CategoryType type = CategoryType.valueOf(request.getType().trim().toUpperCase());
//                    categoryEntity.setType(type);
//                } catch (IllegalArgumentException e) {
//                    throw new UserMessageException("Loại danh mục không hợp lệ (Chỉ nhận CHI hoặc THU).");
//                }
//            }
//            categoryRepository.save(categoryEntity);
//        } else {
//            throw new UserMessageException("Không tìm thấy danh mục để cập nhật!");
//        }
    }
}