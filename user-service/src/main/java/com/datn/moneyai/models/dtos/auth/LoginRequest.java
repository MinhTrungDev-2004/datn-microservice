package com.datn.moneyai.models.dtos.auth;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
}
