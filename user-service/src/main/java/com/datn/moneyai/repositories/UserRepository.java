package com.datn.moneyai.repositories;

import com.datn.moneyai.models.entities.bases.UserEntity;
import com.datn.moneyai.models.entities.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<UserEntity, UUID> {
    boolean existsByUsername(String username);
    Optional<UserEntity> findByUsername(String username);
    List<UserEntity> findAllByRoleNot(UserRole role);
}
