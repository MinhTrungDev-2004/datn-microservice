package com.datn.moneyai.models.dtos.transaction;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.datn.moneyai.models.entities.enums.CategoryType;

import jakarta.validation.constraints.Positive;
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

    private Long categoryId;

    private String categoryName;
    
    private CategoryType categoryType;

    @Positive(message = "Số tiền phải lớn hơn 0")
    private BigDecimal amount;

    private LocalDate transactionDate;

    private String note;
}
