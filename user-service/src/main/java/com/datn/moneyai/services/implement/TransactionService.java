package com.datn.moneyai.services.implement;

import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.List;
import org.springframework.stereotype.Service;
import com.datn.moneyai.models.dtos.transaction.TransactionResponse;
import com.datn.moneyai.models.entities.bases.TransactionEntity;
import com.datn.moneyai.models.entities.bases.CategoryEntity;
import com.datn.moneyai.models.entities.bases.User;
import com.datn.moneyai.models.entities.enums.TransactionSource;
import com.datn.moneyai.models.global.ApiResult;
import com.datn.moneyai.repositories.CategoryRepository;
import com.datn.moneyai.repositories.UserRepository;
import com.datn.moneyai.repositories.TransactionRepository;
import com.datn.moneyai.models.dtos.transaction.TransactionRequest;
import com.datn.moneyai.services.interfaces.ITransactionService;
import com.datn.moneyai.models.dtos.transaction.TransactionUpdateRequest;
import com.datn.moneyai.exceptions.UserMessageException;
import org.springframework.security.core.context.SecurityContextHolder;

@Service
@RequiredArgsConstructor
public class TransactionService implements ITransactionService {
    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    /**
     * Tạo mới một giao dịch chi tiêu hoặc thu nhập.
     *
     * @param request Dữ liệu đầu vào chứa thông tin giao dịch cần tạo (số tiền,
     *                ngày giao dịch, ghi chú, danh mục).
     * @return ApiResult mang theo đối tượng TransactionResponse vừa được tạo thành
     *         công.
     * @throws UserMessageException Nếu dữ liệu yêu cầu không hợp lệ hoặc người dùng
     *                              không có quyền sử dụng danh mục.
     */
    @Override
    public ApiResult<TransactionResponse> createTransaction(TransactionRequest request) {
        if (request == null)
            throw new UserMessageException("Dữ liệu yêu cầu không hợp lệ");
        if (request.getAmount() == null || request.getAmount().signum() <= 0)
            throw new UserMessageException("Số tiền phải lớn hơn 0");
        if (request.getTransactionDate() == null)
            throw new UserMessageException("Vui lòng chọn ngày giao dịch");
        if (request.getCategoryId() == null)
            throw new UserMessageException("Vui lòng chọn danh mục");

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserMessageException("Không tìm thấy người dùng."));

        CategoryEntity category = categoryRepository.findActiveCategoryById(request.getCategoryId())
                .orElseThrow(() -> new UserMessageException("Không tìm thấy danh mục hoặc danh mục đã bị xóa!"));

        if (!Objects.equals(category.getUser().getId(), user.getId())) {
            throw new UserMessageException("Bạn không có quyền sử dụng danh mục này.");
        }

        TransactionEntity transaction = TransactionEntity.builder()
                .totalAmount(request.getAmount())
                .note(request.getNote())
                .transactionDate(request.getTransactionDate() != null ? request.getTransactionDate()
                        : LocalDateTime.now().toLocalDate())
                .category(category)
                .user(user)
                .source(TransactionSource.MANUAL.name())
                .build();

        TransactionEntity savedTransaction = transactionRepository.save(transaction);

        return ApiResult.success(TransactionResponse.builder()
                .id(savedTransaction.getId())
                .categoryId(category.getId())
                .categoryName(category.getName())
                .categoryType(category.getType())
                .amount(savedTransaction.getTotalAmount())
                .note(savedTransaction.getNote())
                .transactionDate(savedTransaction.getTransactionDate())
                .build(), "Tạo giao dịch thành công");
    }

    /**
     * Cập nhật thông tin một giao dịch chi tiêu hoặc thu nhập.
     *
     * @param id      ID của giao dịch cần cập nhật.
     * @param request Dữ liệu đầu vào chứa thông tin giao dịch cần cập nhật (số
     *                tiền, ngày giao dịch, ghi chú, danh mục).
     * @return ApiResult mang theo đối tượng TransactionResponse vừa được cập nhật
     *         thàng
     */
    @Override
    public ApiResult<TransactionResponse> updateTransaction(Long id, TransactionUpdateRequest request) {
        if (id == null)
            throw new UserMessageException("Thiếu id giao dịch");

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserMessageException("Không tìm thấy người dùng."));

        TransactionEntity tx = transactionRepository.findActiveByIdAndUser(id, user.getId())
                .orElseThrow(() -> new UserMessageException("Không tìm thấy giao dịch hoặc đã bị xóa!"));

        if (request.getCategoryId() != null) {
            CategoryEntity category = categoryRepository.findActiveCategoryById(request.getCategoryId())
                    .orElseThrow(() -> new UserMessageException("Không tìm thấy danh mục hoặc danh mục đã bị xóa!"));
            if (!Objects.equals(category.getUser().getId(), user.getId())) {
                throw new UserMessageException("Bạn không có quyền sử dụng danh mục này.");
            }
            tx.setCategory(category);
        }
        if (request.getAmount() != null) {
            if (request.getAmount().signum() <= 0)
                throw new UserMessageException("Số tiền phải lớn hơn 0");
            tx.setTotalAmount(request.getAmount());
        }
        if (request.getTransactionDate() != null) {
            tx.setTransactionDate(request.getTransactionDate());
        }
        if (request.getNote() != null) {
            tx.setNote(request.getNote());
        }

        TransactionEntity saved = transactionRepository.save(tx);
        CategoryEntity c = saved.getCategory();
        return ApiResult.success(TransactionResponse.builder()
                .id(saved.getId())
                .categoryId(c != null ? c.getId() : null)
                .categoryName(c != null ? c.getName() : null)
                .categoryType(c != null ? c.getType() : null)
                .amount(saved.getTotalAmount())
                .note(saved.getNote())
                .transactionDate(saved.getTransactionDate())
                .build(), "Cập nhật giao dịch thành công");
    }

    /**
     * Xóa một giao dịch chi tiêu hoặc thu nhập.
     *
     * @param id ID của giao dịch cần xóa.
     * @return ApiResult mang theo thông báo kết quả xóa.
     * @throws UserMessageException Nếu ID không hợp lệ hoặc giao dịch không tồn tại
     *                              hoặc đã bị xóa.
     */
    @Override
    public ApiResult<Void> deleteTransaction(Long id) {
        if (id == null)
            throw new UserMessageException("Thiếu id giao dịch");
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserMessageException("Không tìm thấy người dùng."));
        TransactionEntity tx = transactionRepository.findActiveByIdAndUser(id, user.getId())
                .orElseThrow(() -> new UserMessageException("Không tìm thấy giao dịch hoặc đã bị xóa!"));
        transactionRepository.save(tx);
        return ApiResult.success(null, "Xóa giao dịch thành công");
    }

    /**
     * Lấy danh sách các giao dịch chi tiêu hoặc thu nhập theo danh mục.
     *
     * @param categoryId ID của danh mục cần lấy giao dịch.
     * @return ApiResult mang theo danh sách TransactionResponse của các giao dịch
     *         thuộc danh mục, hoặc lỗi nếu có vấn đề với dữ liệu yêu cầu hoặc
     *         quyền truy cập.
     */
    @Override
    public ApiResult<List<TransactionResponse>> getTransactionsByCategory(Long categoryId) {
        if (categoryId == null)
            throw new UserMessageException("Thiếu id danh mục");
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserMessageException("Không tìm thấy người dùng."));
        List<TransactionEntity> list = transactionRepository.findAllActiveByCategoryAndUser(categoryId, user.getId());
        List<TransactionResponse> responseList = list.stream().map(t -> {
            CategoryEntity c = t.getCategory();
            return TransactionResponse.builder()
                    .id(t.getId())
                    .categoryId(c != null ? c.getId() : null)
                    .categoryName(c != null ? c.getName() : null)
                    .categoryType(c != null ? c.getType() : null)
                    .amount(t.getTotalAmount())
                    .transactionDate(t.getTransactionDate())
                    .note(t.getNote())
                    .build();
        }).toList();
        return ApiResult.success(responseList, "Lấy danh sách giao dịch theo danh mục thành công");
    }

    /**
     * Lấy tổng số tiền của giao dịch theo danh mục trong tháng hiện tại
     *
     * @param categoryId ID của danh mục cần lấy tổng số tiền.
     * @return ApiResult mang theo tổng số tiền của các giao dịch thuộc danh mục
     *         trong tháng hiện tại, hoặc lỗi nếu có vấn đề với dữ liệu yêu cầu hoặc
     *         quyền truy cập.
     */
    @Override
    public ApiResult<BigDecimal> getTotalAmountByCategoryAndMonth(Long categoryId) {
        if (categoryId == null)
            throw new UserMessageException("Thiếu id danh mục");
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserMessageException("Không tìm thấy người dùng."));
        return ApiResult.success(transactionRepository.sumTotalAmountByCategoryAndMonth(categoryId, user.getId()),
                "Lấy tổng số tiền theo danh mục và tháng thành công");
    }

    /**
     * Lấy tổng số tiền thu nhập của người dùng trong tháng hiện tại
     *
     * @return ApiResult mang theo tổng số tiền thu nhập của người dùng trong tháng
     *         hiện tại, hoặc lỗi nếu có vấn đề với quyền truy cập.
     */
    @Override
    public ApiResult<BigDecimal> calculateTotalIncome() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserMessageException("Không tìm thấy người dùng."));
        return ApiResult.success(transactionRepository.calculateTotalIncome(user.getId()),
                "Lấy tổng thu nhập thành công");
    }

    /**
     * Lấy tổng số tiền chi tiêu của người dùng theo danh mục trong tháng hiện tại
     *
     * @return ApiResult mang theo tổng số tiền chi tiêu của người dùng trong tháng
     *         hiện tại, hoặc lỗi nếu có vấn đề với quyền truy cập.
     */
    @Override
    public ApiResult<BigDecimal> calculateTotalExpense() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserMessageException("Không tìm thấy người dùng."));
        return ApiResult.success(transactionRepository.calculateTotalExpense(user.getId()),
                "Lấy tổng chi tiêu thành công");
    }
}
