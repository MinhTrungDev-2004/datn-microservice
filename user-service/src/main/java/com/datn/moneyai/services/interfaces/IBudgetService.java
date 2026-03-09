package com.datn.moneyai.services.interfaces;

import com.datn.moneyai.models.dtos.budget.BudgetRequest;
import com.datn.moneyai.models.dtos.budget.BudgetResponse;
import com.datn.moneyai.models.global.ApiResult;
import java.util.List;

public interface IBudgetService {
    ApiResult<BudgetResponse> createBudget(BudgetRequest request);

    ApiResult<BudgetResponse> updateBudget(Long id, BudgetRequest request);

    ApiResult<BudgetResponse> getBudget(Long id);

    ApiResult<BudgetResponse> getBudgetByCategory(Long categoryId, Integer month, Integer year);

    ApiResult<List<BudgetResponse>> listBudgets(Integer month, Integer year);

    ApiResult<Void> deleteBudget(Long id);
}
