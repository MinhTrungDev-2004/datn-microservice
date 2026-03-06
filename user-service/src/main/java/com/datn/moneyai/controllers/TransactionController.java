package com.datn.moneyai.controllers;

import org.springframework.web.bind.annotation.RestController;

import com.datn.moneyai.models.dtos.transaction.TransactionRequest;
import com.datn.moneyai.models.dtos.transaction.TransactionResponse;
import com.datn.moneyai.models.global.ApiResult;
import com.datn.moneyai.services.interfaces.ITransactionService;
import lombok.AllArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@AllArgsConstructor
@RequestMapping("/transaction")
public class TransactionController {
    private final ITransactionService transactionService;

    /*
     * API tạo mới danh mục category
     */
    @PostMapping("/create")
    public ResponseEntity<ApiResult<TransactionResponse>> postMethodName(@RequestBody TransactionRequest request) {
        TransactionResponse data = transactionService.createTransaction(request);
        return ResponseEntity.ok(ApiResult.success(data, "Tạo giao dịch thành công"));
    }

}
