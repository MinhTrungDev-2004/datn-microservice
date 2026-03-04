package com.datn.moneyai.models.dtos.auth;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginGetResponse {

    private String fullName;

    private String email;

    private String avatarUrl;

    private String role;
}
