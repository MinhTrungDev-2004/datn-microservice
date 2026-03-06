package com.datn.moneyai.controllers;

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
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@AllArgsConstructor
@RequestMapping("/transaction")
public class TransactionController {
    private final ITransactionService transactionService;

    /*
     * API tạo mới danh mục transaction (giao dịch)
     */
    @PostMapping("/create")
    public ResponseEntity<ApiResult<TransactionResponse>> createTransaction(@Valid @RequestBody TransactionRequest request) {
        TransactionResponse data = transactionService.createTransaction(request);
        return ResponseEntity.ok(ApiResult.success(data, "Tạo giao dịch thành công"));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResult<TransactionResponse>> updateTransaction(
            @PathVariable Long id,
            @Valid @RequestBody TransactionUpdateRequest request) {
        TransactionResponse data = transactionService.updateTransaction(id, request);
        return ResponseEntity.ok(ApiResult.success(data, "Cập nhật giao dịch thành công"));
    }

    /*
     * API xóa giao dịch theo id
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResult<Void>> deleteTransaction(@PathVariable Long id) {
        transactionService.deleteTransaction(id);
        return ResponseEntity.ok(ApiResult.success(null, "Xóa giao dịch thành công"));
    }

    @GetMapping("/by-category/{categoryId}")
    public ResponseEntity<ApiResult<List<TransactionResponse>>> getByCategory(@PathVariable Long categoryId) {
        List<TransactionResponse> data = transactionService.getTransactionsByCategory(categoryId);
        return ResponseEntity.ok(ApiResult.success(data, "Lấy danh sách giao dịch theo danh mục thành công"));
    }
}
