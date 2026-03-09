package com.datn.moneyai.models.entities.bases;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "emails_raw")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailRaw extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String messageId;

    private String subject;

    private String sender;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Builder.Default
    private boolean processed = false;
}
