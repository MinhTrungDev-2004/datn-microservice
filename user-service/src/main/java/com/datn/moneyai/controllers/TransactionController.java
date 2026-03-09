package com.datn.moneyai.controllers;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.web.bind.annotation.RestController;
import com.datn.moneyai.models.dtos.transaction.TransactionRequest;
import com.datn.moneyai.models.dtos.transaction.TransactionResponse;
import com.datn.moneyai.models.dtos.transaction.TransactionUpdateRequest;
import com.datn.moneyai.models.global.ApiResult;
import com.datn.moneyai.services.interfaces.ITransactionService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@AllArgsConstructor
@RequestMapping("/transaction")
public class TransactionController extends ApiBaseController {

    private final ITransactionService transactionService;

    /**
     * API tạo mới một giao dịch (Transaction).
     *
     * @param request Dữ liệu đầu vào chứa thông tin giao dịch cần tạo.
     * @return ResponseEntity chứa ApiResult mang theo đối tượng TransactionResponse
     *         vừa tạo.
     */
    @PostMapping("/create")
    public ResponseEntity<ApiResult<TransactionResponse>> createTransaction(
            @Valid @RequestBody TransactionRequest request) {
        return exeResponseEntity(() -> transactionService.createTransaction(request));
    }

    /**
     * API cập nhật thông tin của một giao dịch.
     *
     * @param id      ID của giao dịch cần cập nhật.
     * @param request Dữ liệu cập nhật mới cho giao dịch.
     * @return ResponseEntity chứa ApiResult mang theo đối tượng TransactionResponse
     *         sau khi cập nhật.
     */
    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResult<TransactionResponse>> updateTransaction(
            @PathVariable Long id,
            @Valid @RequestBody TransactionUpdateRequest request) {
        return exeResponseEntity(() -> transactionService.updateTransaction(id, request));
    }

    /**
     * API xóa một giao dịch theo ID.
     *
     * @param id ID của giao dịch cần xóa.
     * @return ResponseEntity chứa ApiResult trạng thái thành công với dữ liệu là
     *         Void.
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResult<Void>> deleteTransaction(@PathVariable Long id) {
        return exeResponseEntity(() -> transactionService.deleteTransaction(id));
    }

    /**
     * API lấy danh sách tất cả các giao dịch thuộc về một danh mục cụ thể.
     *
     * @param categoryId ID của danh mục cần tra cứu giao dịch.
     * @return ResponseEntity chứa ApiResult mang theo danh sách (List) các
     *         TransactionResponse.
     */
    @GetMapping("/gets-all/{categoryId}")
    public ResponseEntity<ApiResult<List<TransactionResponse>>> getTransactionsByCategory(
            @PathVariable Long categoryId) {
        return exeResponseEntity(() -> transactionService.getTransactionsByCategory(categoryId));
    }

    /**
     * API tính tổng số tiền giao dịch của một danh mục trong tháng hiện tại.
     *
     * @param categoryId ID của danh mục cần tính tổng.
     * @return ResponseEntity chứa ApiResult mang theo giá trị tổng (BigDecimal).
     */
    @GetMapping("/total-amount/{categoryId}")
    public ResponseEntity<ApiResult<BigDecimal>> getTotalAmountByCategoryAndMonth(@PathVariable Long categoryId) {
        return exeResponseEntity(() -> transactionService.getTotalAmountByCategoryAndMonth(categoryId));
    }

    /**
     * API tính tổng toàn bộ thu nhập (Income) của người dùng trong tháng hiện tại.
     *
     * @return ResponseEntity chứa ApiResult mang theo giá trị tổng thu nhập
     *         (BigDecimal).
     */
    @GetMapping("/total-money-income")
    public ResponseEntity<ApiResult<BigDecimal>> calculateTotalIncome() {
        return exeResponseEntity(() -> transactionService.calculateTotalIncome());
    }

    /**
     * API tính tổng toàn bộ chi tiêu (Expense) của người dùng trong tháng hiện tại.
     *
     * @return ResponseEntity chứa ApiResult mang theo giá trị tổng chi tiêu
     *         (BigDecimal).
     */
    @GetMapping("/total-money-expense")
    public ResponseEntity<ApiResult<BigDecimal>> calculateTotalExpense() {
        return exeResponseEntity(() -> transactionService.calculateTotalExpense());
    }
}