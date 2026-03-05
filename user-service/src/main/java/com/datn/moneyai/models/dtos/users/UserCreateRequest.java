package com.datn.moneyai.models.dtos.users;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateRequest {

    @Schema(example = "Ngô Minh Trung")
    @NotBlank(message = "Vui lòng nhập tên")
    private String name;

    @Schema(example = "pa@gmail.com")
    @NotBlank(message = "Vui lòng nhập email")
    private String email;

    @Schema(example = "123456789")
    @NotBlank(message = "Vui lòng nhập mật khẩu")
    private String password;

    @Schema(example = "USER")
    @NotBlank(message = "Vui lòng nhập vai trò")
    private String role;

    @Schema(example = "")
    private String avatarUrl;

    @Schema(example = "VND")
    private String defaultCurrency;

    @Schema(example = "true")
    private Boolean isActive;
}
