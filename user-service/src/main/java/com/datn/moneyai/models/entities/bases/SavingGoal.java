package com.datn.moneyai.models.entities.bases;

import com.datn.moneyai.models.entities.enums.SavingGoalStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "saving_goals")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SavingGoal extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal targetAmount;

    private LocalDate deadlineDate;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    @Builder.Default
    private SavingGoalStatus status = SavingGoalStatus.ONGOING;
}
