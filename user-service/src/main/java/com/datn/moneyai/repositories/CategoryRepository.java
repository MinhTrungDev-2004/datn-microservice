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
    @Query(value = "SELECT * FROM categories WHERE id = :id", nativeQuery = true)
    Optional<CategoryEntity> findCategoryById(@Param("id") Long id);

    @Query(value = "SELECT * FROM categories WHERE id = :id", nativeQuery = true)
    Optional<CategoryEntity> findActiveCategoryById(@Param("id") Long id);

    @Query(value = "SELECT * FROM categories", nativeQuery = true)
    List<CategoryEntity> findAllActiveCategories();
}
