package com.datn.moneyai.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.datn.moneyai.models.entities.bases.TransactionEntity;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface TransactionRepository extends JpaRepository<TransactionEntity, Long> {

    @Query("SELECT t FROM TransactionEntity t WHERE t.id = :id AND t.user.id = :userId")
    Optional<TransactionEntity> findActiveByIdAndUser(@Param("id") Long id, @Param("userId") Long userId);

    @Query("SELECT t FROM TransactionEntity t WHERE t.category.id = :categoryId AND t.user.id = :userId")
    List<TransactionEntity> findAllActiveByCategoryAndUser(@Param("categoryId") Long categoryId,
            @Param("userId") Long userId);

    @Query("SELECT COALESCE(SUM(t.totalAmount), 0) FROM TransactionEntity t " +
            "WHERE t.category.id = :categoryId AND t.user.id = :userId " +
            "AND MONTH(t.transactionDate) = MONTH(CURRENT_DATE) " +
            "AND YEAR(t.transactionDate) = YEAR(CURRENT_DATE)")
    BigDecimal sumTotalAmountByCategoryAndMonth(@Param("categoryId") Long categoryId, @Param("userId") Long userId);

    @Query("SELECT COALESCE(SUM(t.totalAmount), 0) FROM TransactionEntity t " +
            "WHERE t.user.id = :userId " +
            "AND t.category.type = 'THU' " +
            "AND MONTH(t.transactionDate) = MONTH(CURRENT_DATE) " +
            "AND YEAR(t.transactionDate) = YEAR(CURRENT_DATE) " +
            "AND t.totalAmount > 0")
    BigDecimal calculateTotalIncome(@Param("userId") Long userId);

    @Query("SELECT COALESCE(SUM(t.totalAmount), 0) FROM TransactionEntity t " +
            "WHERE t.user.id = :userId " +
            "AND t.category.type = 'CHI' " +
            "AND MONTH(t.transactionDate) = MONTH(CURRENT_DATE) " +
            "AND YEAR(t.transactionDate) = YEAR(CURRENT_DATE) " +
            "AND t.totalAmount > 0")
    BigDecimal calculateTotalExpense(@Param("userId") Long userId);
}
