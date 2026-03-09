package com.datn.moneyai.services.implement;

import com.datn.moneyai.exceptions.UserMessageException;
import com.datn.moneyai.models.dtos.budget.BudgetRequest;
import com.datn.moneyai.models.dtos.budget.BudgetResponse;
import com.datn.moneyai.models.entities.bases.Budget;
import com.datn.moneyai.models.entities.bases.CategoryEntity;
import com.datn.moneyai.models.entities.bases.User;
import com.datn.moneyai.models.global.ApiResult;
import com.datn.moneyai.repositories.BudgetRepository;
import com.datn.moneyai.repositories.CategoryRepository;
import com.datn.moneyai.repositories.UserRepository;
import com.datn.moneyai.services.interfaces.IBudgetService;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BudgetService implements IBudgetService {
    private final BudgetRepository budgetRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    /**
     * Tạo mới một ngân sách.
     *
     * @param request Dữ liệu đầu vào chứa thông tin ngân sách cần tạo (tháng,
     *                năm, số tiền, danh mục).
     * @return ApiResult mang theo đối tượng BudgetResponse vừa được tạo.
     * @throws UserMessageException Nếu thiếu thông tin bắt buộc hoặc không tìm
     *                              thấy danh mục.
     */
    @Override
    public ApiResult<BudgetResponse> createBudget(BudgetRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserMessageException("Không tìm thấy người dùng."));

        if (request.getCategoryId() == null) {
            throw new UserMessageException("Thiếu danh mục.");
        }
        if (request.getMonth() == null || request.getYear() == null) {
            throw new UserMessageException("Thiếu tháng/năm.");
        }

        CategoryEntity category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new UserMessageException("Không tìm thấy danh mục."));
        if (!category.getUser().getId().equals(user.getId())) {
            throw new UserMessageException("Bạn không có quyền đặt ngân sách cho danh mục này.");
        }

        budgetRepository.findByUserAndCategoryAndMonthAndYear(user.getId(), category.getId(),
                request.getMonth(), request.getYear())
                .ifPresent(b -> {
                    throw new UserMessageException("Ngân sách cho danh mục đã tồn tại trong tháng/năm này.");
                });

        Budget budget = Budget.builder()
                .user(user)
                .category(category)
                .limitAmount(request.getLimitAmount())
                .month(request.getMonth())
                .year(request.getYear())
                .build();
        Budget saved = budgetRepository.save(budget);
        return ApiResult.success(toResponse(saved), "Tạo ngân sách thành công");
    }

    /**
     * Cập nhật thông tin ngân sách.
     *
     * @param id      ID của ngân sách cần cập nhật.
     * @param request Dữ liệu đầu vào chứa thông tin ngân sách cần cập nhật.
     * @return ApiResult mang theo đối tượng BudgetResponse vừa được cập nhật.
     * @throws UserMessageException Nếu không tìm thấy ngân sách hoặc danh mục mới
     *                              không hợp lệ.
     */
    @Override
    public ApiResult<BudgetResponse> updateBudget(Long id, BudgetRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserMessageException("Không tìm thấy người dùng."));

        Budget budget = budgetRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new UserMessageException("Không tìm thấy ngân sách."));

        if (request.getLimitAmount() != null) {
            budget.setLimitAmount(request.getLimitAmount());
        }
        if (request.getMonth() != null) {
            budget.setMonth(request.getMonth());
        }
        if (request.getYear() != null) {
            budget.setYear(request.getYear());
        }
        if (request.getCategoryId() != null) {
            CategoryEntity category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new UserMessageException("Không tìm thấy danh mục."));
            if (!category.getUser().getId().equals(user.getId())) {
                throw new UserMessageException("Bạn không có quyền gán danh mục này.");
            }
            budget.setCategory(category);
        }

        budgetRepository.findByUserAndCategoryAndMonthAndYear(user.getId(), budget.getCategory().getId(),
                budget.getMonth(), budget.getYear())
                .ifPresent(existing -> {
                    if (!existing.getId().equals(budget.getId())) {
                        throw new UserMessageException("Đã tồn tại ngân sách trùng danh mục/tháng/năm.");
                    }
                });

        Budget saved = budgetRepository.save(budget);
        return ApiResult.success(toResponse(saved), "Cập nhật ngân sách thành công");
    }

    /**
     * Lấy thông tin một ngân sách theo ID.
     *
     * @param id ID của ngân sách cần lấy thông tin.
     * @return ApiResult mang theo đối tượng BudgetResponse vừa được lấy.
     * @throws UserMessageException Nếu không tìm thấy ngân sách hoặc người dùng
     *                              không có quyền truy cập.
     */
    @Override
    public ApiResult<BudgetResponse> getBudget(Long id) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserMessageException("Không tìm thấy người dùng."));

        Budget budget = budgetRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new UserMessageException("Không tìm thấy ngân sách."));
        return ApiResult.success(toResponse(budget), "Lấy ngân sách thành công");
    }

    /**
     * Lấy thông tin ngân sách theo danh mục.
     *
     * @param categoryId ID của danh mục.
     * @param month      Tháng cần lọc.
     * @param year       Năm cần lọc.
     * @return ApiResult mang theo đối tượng BudgetResponse vừa được lấy.
     * @throws UserMessageException Nếu không tìm thấy ngân sách hoặc người dùng
     *                              không có quyền truy cập.
     */
    @Override
    public ApiResult<BudgetResponse> getBudgetByCategory(Long categoryId, Integer month, Integer year) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserMessageException("Không tìm thấy người dùng."));

        if (categoryId == null) {
            throw new UserMessageException("Thiếu danh mục.");
        }
        LocalDate now = LocalDate.now();
        int m = month != null ? month : now.getMonthValue();
        int y = year != null ? year : now.getYear();

        Budget budget = budgetRepository.findByUserAndCategoryAndMonthAndYear(user.getId(), categoryId, m, y)
                .orElseThrow(() -> new UserMessageException("Không tìm thấy ngân sách theo danh mục."));
        return ApiResult.success(toResponse(budget), "Lấy ngân sách theo danh mục thành công");
    }

    /**
     * Lấy danh sách tất cả ngân sách.
     *
     * @param month Tháng cần lọc.
     * @param year  Năm cần lọc.
     * @return ApiResult mang theo danh sách đối tượng BudgetResponse.
     * @throws UserMessageException Nếu không tìm thấy ngân sách hoặc người dùng
     *                              không có quyền truy cập.
     */
    @Override
    public ApiResult<List<BudgetResponse>> listBudgets(Integer month, Integer year) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserMessageException("Không tìm thấy người dùng."));

        LocalDate now = LocalDate.now();
        int m = month != null ? month : now.getMonthValue();
        int y = year != null ? year : now.getYear();

        return ApiResult.success(budgetRepository.findAllByUserAndMonthAndYear(user.getId(), m, y)
                .stream().map(this::toResponse).collect(Collectors.toList()), "Lấy danh sách ngân sách thành công");
    }

    /**
     * Xóa ngân sách theo ID.
     *
     * @param id ID của ngân sách cần xóa.
     * @return ApiResult mang theo null nếu xóa thành công.
     * @throws UserMessageException Nếu không tìm thấy ngân sách hoặc người dùng
     *                              không có quyền truy cập.
     */
    @Override
    public ApiResult<Void> deleteBudget(Long id) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserMessageException("Không tìm thấy người dùng."));

        Budget budget = budgetRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new UserMessageException("Không tìm thấy ngân sách."));
        budgetRepository.delete(budget);
        return ApiResult.success(null, "Xóa ngân sách thành công");
    }

    /**
     * Chuyển đổi từ đối tượng Budget sang BudgetResponse.
     *
     * @param budget Đối tượng Budget cần chuyển đổi.
     * @return Đối tượng BudgetResponse tương ứng với Budget đầu vào.
     */
    private BudgetResponse toResponse(Budget budget) {
        BudgetResponse res = new BudgetResponse();
        res.setId(budget.getId());
        res.setLimitAmount(budget.getLimitAmount());
        res.setMonth(budget.getMonth());
        res.setYear(budget.getYear());
        res.setCategoryId(budget.getCategory() != null ? budget.getCategory().getId() : null);
        res.setCategoryName(budget.getCategory() != null ? budget.getCategory().getName() : null);
        res.setCreatedAt(budget.getCreatedAt());
        res.setUpdatedAt(budget.getUpdatedAt());
        return res;
    }
}
