package com.datn.moneyai.repositories;

import com.datn.moneyai.models.entities.bases.Budget;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {

    @Query(value = "SELECT * FROM budgets WHERE id = :id AND user_id = :userId", nativeQuery = true)
    Optional<Budget> findByIdAndUserId(@Param("id") Long id, @Param("userId") Long userId);


    @Query(value = "SELECT * FROM budgets WHERE user_id = :userId AND category_id = :categoryId AND month = :month AND year = :year", nativeQuery = true)
    Optional<Budget> findByUserAndCategoryAndMonthAndYear(@Param("userId") Long userId,
                                                          @Param("categoryId") Long categoryId,
                                                          @Param("month") Integer month,
                                                          @Param("year") Integer year);

    @Query(value = "SELECT * FROM budgets WHERE user_id = :userId AND month = :month AND year = :year", nativeQuery = true)
    List<Budget> findAllByUserAndMonthAndYear(@Param("userId") Long userId,
                                              @Param("month") Integer month,
                                              @Param("year") Integer year);
}
