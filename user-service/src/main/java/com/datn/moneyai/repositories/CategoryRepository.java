package com.datn.moneyai.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;

import com.datn.moneyai.models.entities.bases.CategoryEntity;

@Repository
public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {
    @Query("SELECT c FROM CategoryEntity c WHERE c.id = :id")
    Optional<CategoryEntity> findCategoryById(@Param("id") Long id);

    @Query("SELECT c FROM CategoryEntity c WHERE c.id = :id")
    Optional<CategoryEntity> findActiveCategoryById(@Param("id") Long id);

    @Query("SELECT c FROM CategoryEntity c")
    List<CategoryEntity> findAllActiveCategories();
}
