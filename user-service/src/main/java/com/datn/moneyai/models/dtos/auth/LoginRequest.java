package com.datn.moneyai.models.dtos.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class LoginRequest {

    @Schema(example = "phamphuonganh@gmail.com")
    private String email;

    @Schema(example = "123456789")
    private String password;
}
