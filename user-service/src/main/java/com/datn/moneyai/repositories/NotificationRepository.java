package com.datn.moneyai.repositories;

import com.datn.moneyai.models.entities.bases.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    @Query(value = "SELECT n.* FROM notifications n INNER JOIN user u ON n.user_id = u.id WHERE u.email = :email ORDER BY n.created_at DESC", nativeQuery = true)
    List<Notification> findByUserEmailOrderByCreatedAtDesc(@Param("email") String email);

    @Query(value = "SELECT * FROM notifications WHERE user_id = :userId ORDER BY created_at DESC", nativeQuery = true)
    List<Notification> findByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId);
}
