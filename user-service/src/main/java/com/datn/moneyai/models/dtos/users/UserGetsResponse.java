package com.datn.moneyai.models.dtos.users;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserGetsResponse {
    private UUID id;
    private String username;
    private String role;
    private LocalDateTime createTime;
    private LocalDateTime lastModifiedTime;
}