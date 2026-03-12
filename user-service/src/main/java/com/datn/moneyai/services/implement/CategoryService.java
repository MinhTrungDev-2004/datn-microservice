package com.datn.moneyai.services.implement;

import com.datn.moneyai.exceptions.UserMessageException;
import com.datn.moneyai.models.dtos.category.CategoryRequest;
import com.datn.moneyai.models.dtos.category.CategoryResponse;
import com.datn.moneyai.models.entities.bases.CategoryEntity;
import com.datn.moneyai.models.entities.bases.User;
import com.datn.moneyai.models.entities.enums.CategoryType;
import com.datn.moneyai.models.global.ApiResult;
import com.datn.moneyai.repositories.CategoryRepository;
import com.datn.moneyai.repositories.UserRepository;
import com.datn.moneyai.services.interfaces.ICategoryService;
import lombok.RequiredArgsConstructor;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryService implements ICategoryService {
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    /**
     * Tạo mới một danh mục chi tiêu hoặc thu nhập.
     *
     * @param request Dữ liệu đầu vào chứa thông tin danh mục cần tạo (tên, loại,
     *                biểu tượng, mã màu).
     * @return ApiResult mang theo đối tượng CategoryResponse vừa được tạo thành
     *         công.
     * @throws UserMessageException Nếu người dùng không tồn tại hoặc loại danh mục
     *                              không hợp lệ.
     */
    @Override
    public ApiResult<CategoryResponse> createCategory(CategoryRequest request) {
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
            throw new UserMessageException("Loại danh mục không hợp lệ (Chỉ nhận EXPENSE hoặc INCOME).");
        }

        CategoryEntity newCategory = CategoryEntity.builder()
                .name(request.getName())
                .icon(request.getIcon())
                .colorCode(request.getColorCode())
                .type(type)
                .user(user)
                .build();
        CategoryEntity saved = categoryRepository.save(newCategory);
        return ApiResult.success(CategoryResponse.builder()
                .id(saved.getId())
                .name(saved.getName())
                .type(saved.getType())
                .icon(saved.getIcon())
                .colorCode(saved.getColorCode())
                .createdAt(saved.getCreatedAt())
                .updatedAt(saved.getUpdatedAt())
                .build(), "Tạo danh mục thành công");
    }

    /**
     * Cập nhật thông tin danh mục chi tiêu hoặc thu nhập.
     *
     * @param categoryId ID của danh mục cần cập nhật.
     * @param request    Dữ liệu đầu vào chứa thông tin danh mục cần cập nhật (tên,
     *                   loại,
     *                   biểu tượng, mã màu). * @return ApiResult mang theo đối
     *                   tượng CategoryResponse vừa được cập nhật.
     */
    @Override
    public ApiResult<CategoryResponse> updateCategory(Long categoryId, CategoryRequest request) {
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
        return ApiResult.success(CategoryResponse.builder()
                .id(saved.getId())
                .name(saved.getName())
                .type(saved.getType())
                .icon(saved.getIcon())
                .colorCode(saved.getColorCode())
                .createdAt(saved.getCreatedAt())
                .updatedAt(saved.getUpdatedAt())
                .build(), "Cập nhật danh mục thành công");
    }

    /**
     * Lấy danh sách tất cả danh mục chi tiêu và thu nhập của người dùng hiện tại.
     *
     * @return ApiResult mang theo danh sách đối tượng CategoryResponse.
     * @throws UserMessageException Nếu người dùng không tồn tại hoặc không có
     *                              danh mục nào.
     */
    @Override
    public ApiResult<List<CategoryResponse>> getsCategory() {
        List<CategoryEntity> categories = categoryRepository.findAllActiveCategories();
        return ApiResult.success(categories.stream()
                .map(category -> CategoryResponse.builder()
                        .id(category.getId())
                        .name(category.getName())
                        .type(category.getType())
                        .icon(category.getIcon())
                        .colorCode(category.getColorCode())
                        .build())
                .collect(Collectors.toList()), "Lấy danh mục thành công");
    }

    /**
     * Xóa một danh mục chi tiêu hoặc thu nhập theo ID.
     *
     * @param id ID của danh mục cần xóa.
     * @return ApiResult mang theo null nếu xóa thành công.
     * @throws UserMessageException Nếu không tìm thấy danh mục hoặc người dùng
     *                              không có quyền xóa.
     */
    @Override
    public ApiResult<Void> deleteCategory(Long id) {
        CategoryEntity existingCategory = categoryRepository.findActiveCategoryById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục hoặc danh mục đã bị xóa!"));
        categoryRepository.save(existingCategory);
        return ApiResult.success(null, "Xóa danh mục thành công");
    }
}
