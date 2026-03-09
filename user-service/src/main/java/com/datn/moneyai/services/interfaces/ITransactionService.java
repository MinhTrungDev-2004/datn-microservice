package com.datn.moneyai.services.interfaces;

import com.datn.moneyai.models.dtos.transaction.TransactionRequest;
import com.datn.moneyai.models.dtos.transaction.TransactionResponse;
import com.datn.moneyai.models.dtos.transaction.TransactionUpdateRequest;
import com.datn.moneyai.models.global.ApiResult;

import java.math.BigDecimal;
import java.util.List;

public interface ITransactionService {
    ApiResult<TransactionResponse> createTransaction(TransactionRequest request);

    ApiResult<TransactionResponse> updateTransaction(Long id, TransactionUpdateRequest request);

    ApiResult<Void> deleteTransaction(Long id);

    ApiResult<List<TransactionResponse>> getTransactionsByCategory(Long categoryId);

    /**
     * Lấy tổng số tiền của giao dịch theo danh mục trong tháng hiện tại
     * 
     * @param categoryId the ID of the category
     * @return the total amount
     */
    ApiResult<BigDecimal> getTotalAmountByCategoryAndMonth(Long categoryId);

    /**
     * Lấy tổng số tiền thu nhập của người dùng trong tháng hiện tại
     * 
     * @return the total income
     */
    ApiResult<BigDecimal> calculateTotalIncome();

    /**
     * Lấy tổng số tiền chi tiêu của người dùng theo danh mục trong tháng hiện tại
     * 
     * @return the total expense
     */
    ApiResult<BigDecimal> calculateTotalExpense();
}
