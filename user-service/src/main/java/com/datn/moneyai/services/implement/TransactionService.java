package com.datn.moneyai.services.implement;

import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.datn.moneyai.models.dtos.transaction.TransactionResponse;
import com.datn.moneyai.models.entities.bases.TransactionEntity;
import com.datn.moneyai.repositories.TransactionRepository;
import com.datn.moneyai.models.dtos.transaction.TransactionRequest;
import com.datn.moneyai.services.interfaces.ITransactionService;

@Service
@RequiredArgsConstructor
public class TransactionService implements ITransactionService {
    private final TransactionRepository transactionRepository;

    @Override
    public TransactionResponse createTransaction(TransactionRequest request) {
        if (request == null || request.getAmount() == null || request.getCategory() == null) {
            throw new IllegalArgumentException("Amount and Category are required fields");
        }

        TransactionEntity transaction = TransactionEntity.builder()
                .amount(request.getAmount())
                .note(request.getNote())
                .transactionDate(request.getTransactionDate() != null ? request.getTransactionDate()
                        : LocalDateTime.now().toLocalDate())
                .category(request.getCategory())
                .build();

        TransactionEntity savedTransaction = transactionRepository.save(transaction);

        return TransactionResponse.builder()
                .id(savedTransaction.getId())
                .amount(savedTransaction.getAmount())
                .note(savedTransaction.getNote())
                .transactionDate(savedTransaction.getTransactionDate())
                .category(savedTransaction.getCategory())
                .build();
    }
}
