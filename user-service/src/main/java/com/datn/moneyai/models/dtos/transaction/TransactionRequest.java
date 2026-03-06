package com.datn.moneyai.models.dtos.transaction;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.datn.moneyai.models.entities.bases.CategoryEntity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class TransactionRequest {

    private CategoryEntity category;

    private BigDecimal amount;

    private LocalDate transactionDate;

    private String note;
}
