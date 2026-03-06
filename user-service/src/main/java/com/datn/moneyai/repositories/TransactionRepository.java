package com.datn.moneyai.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.datn.moneyai.models.entities.bases.TransactionEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface TransactionRepository extends JpaRepository<TransactionEntity, Long> {

    @Query("SELECT t FROM TransactionEntity t WHERE t.id = :id AND t.user.id = :userId AND (t.isDeleted = false OR t.isDeleted IS NULL)")
    Optional<TransactionEntity> findActiveByIdAndUser(@Param("id") Long id, @Param("userId") Long userId);

    @Query("SELECT t FROM TransactionEntity t WHERE t.category.id = :categoryId AND t.user.id = :userId AND (t.isDeleted = false OR t.isDeleted IS NULL)")
    List<TransactionEntity> findAllActiveByCategoryAndUser(@Param("categoryId") Long categoryId, @Param("userId") Long userId);
}
