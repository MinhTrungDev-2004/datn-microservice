package com.datn.moneyai.models.dtos.transaction;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class TransactionUpdateRequest {
    private Long categoryId;

    @Positive(message = "Số tiền phải lớn hơn 0")
    private BigDecimal amount;

    private LocalDate transactionDate;

    private String note;
}
