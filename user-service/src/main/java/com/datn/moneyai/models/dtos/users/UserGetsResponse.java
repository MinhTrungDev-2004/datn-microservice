package com.datn.moneyai.models.dtos.users;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserGetsResponse {
    private Long id;

    private String email;

    private String role;

    private String avatarUrl;

    private String defaultCurrency;

    private LocalDateTime createTime;

    private LocalDateTime lastModifiedTime;
}