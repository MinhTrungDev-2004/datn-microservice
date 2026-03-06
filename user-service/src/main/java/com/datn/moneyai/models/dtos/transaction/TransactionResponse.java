package com.datn.moneyai.models.dtos.transaction;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.datn.moneyai.models.entities.bases.CategoryEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponse {

    private Long id;

    private CategoryEntity category;

    private BigDecimal amount;

    private LocalDate transactionDate;

    private String note;
}
