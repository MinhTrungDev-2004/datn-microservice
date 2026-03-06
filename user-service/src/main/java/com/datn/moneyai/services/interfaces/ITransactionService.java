package com.datn.moneyai.services.interfaces;

import com.datn.moneyai.models.dtos.transaction.TransactionRequest;
import com.datn.moneyai.models.dtos.transaction.TransactionResponse;
import com.datn.moneyai.models.dtos.transaction.TransactionUpdateRequest;
import java.util.List;

public interface ITransactionService {
    TransactionResponse createTransaction(TransactionRequest request);
    TransactionResponse updateTransaction(Long id, TransactionUpdateRequest request);
    void deleteTransaction(Long id);
    List<TransactionResponse> getTransactionsByCategory(Long categoryId);
}
