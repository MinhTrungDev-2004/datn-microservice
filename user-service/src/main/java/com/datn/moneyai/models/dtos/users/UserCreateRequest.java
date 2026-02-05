package com.datn.moneyai.models.dtos.users;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateRequest {
    @NotBlank(message = "Vui long nhập tên đăng nhập")
    private String username;

    @NotBlank(message = "Vui lòng nhập mật khẩu")
    private String password;

    @NotBlank(message = "Vui lòng nhập vai trò")
    private String role;
}
