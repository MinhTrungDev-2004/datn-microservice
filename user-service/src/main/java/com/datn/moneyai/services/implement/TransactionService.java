package com.datn.moneyai.services.implement;

import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.List;

import org.springframework.stereotype.Service;

import com.datn.moneyai.models.dtos.transaction.TransactionResponse;
import com.datn.moneyai.models.entities.bases.TransactionEntity;
import com.datn.moneyai.models.entities.bases.CategoryEntity;
import com.datn.moneyai.models.entities.bases.User;
import com.datn.moneyai.models.entities.enums.TransactionSource;
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

    @Override
    public TransactionResponse createTransaction(TransactionRequest request) {
        if (request == null) throw new UserMessageException("Dữ liệu yêu cầu không hợp lệ");
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
                .amount(request.getAmount())
                .note(request.getNote())
                .transactionDate(request.getTransactionDate() != null ? request.getTransactionDate()
                        : LocalDateTime.now().toLocalDate())
                .category(category)
                .user(user)
                .source(TransactionSource.MANUAL.name())
                .build();

        TransactionEntity savedTransaction = transactionRepository.save(transaction);

        return TransactionResponse.builder()
                .id(savedTransaction.getId())
                .categoryId(category.getId())
                .categoryName(category.getName())
                .categoryType(category.getType())
                .amount(savedTransaction.getAmount())
                .note(savedTransaction.getNote())
                .transactionDate(savedTransaction.getTransactionDate())
                .build();
    }

    @Override
    public TransactionResponse updateTransaction(Long id, TransactionUpdateRequest request) {
        if (id == null) throw new UserMessageException("Thiếu id giao dịch");

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
            if (request.getAmount().signum() <= 0) throw new UserMessageException("Số tiền phải lớn hơn 0");
            tx.setAmount(request.getAmount());
        }
        if (request.getTransactionDate() != null) {
            tx.setTransactionDate(request.getTransactionDate());
        }
        if (request.getNote() != null) {
            tx.setNote(request.getNote());
        }

        TransactionEntity saved = transactionRepository.save(tx);
        CategoryEntity c = saved.getCategory();
        return TransactionResponse.builder()
                .id(saved.getId())
                .categoryId(c != null ? c.getId() : null)
                .categoryName(c != null ? c.getName() : null)
                .categoryType(c != null ? c.getType() : null)
                .amount(saved.getAmount())
                .note(saved.getNote())
                .transactionDate(saved.getTransactionDate())
                .build();
    }

    @Override
    public void deleteTransaction(Long id) {
        if (id == null) throw new UserMessageException("Thiếu id giao dịch");
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserMessageException("Không tìm thấy người dùng."));
        TransactionEntity tx = transactionRepository.findActiveByIdAndUser(id, user.getId())
                .orElseThrow(() -> new UserMessageException("Không tìm thấy giao dịch hoặc đã bị xóa!"));
        tx.setDeleted(true);
        transactionRepository.save(tx);
    }

    @Override
    public java.util.List<TransactionResponse> getTransactionsByCategory(Long categoryId) {
        if (categoryId == null) throw new UserMessageException("Thiếu id danh mục");
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserMessageException("Không tìm thấy người dùng."));
        List<TransactionEntity> list = transactionRepository.findAllActiveByCategoryAndUser(categoryId, user.getId());
        return list.stream().map(t -> {
            CategoryEntity c = t.getCategory();
            return TransactionResponse.builder()
                    .id(t.getId())
                    .categoryId(c != null ? c.getId() : null)
                    .categoryName(c != null ? c.getName() : null)
                    .categoryType(c != null ? c.getType() : null)
                    .amount(t.getAmount())
                    .transactionDate(t.getTransactionDate())
                    .note(t.getNote())
                    .build();
        }).toList();
    }
}
