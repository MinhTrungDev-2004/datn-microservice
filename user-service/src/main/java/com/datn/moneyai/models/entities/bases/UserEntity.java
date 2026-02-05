package com.datn.moneyai.models.entities.bases;

import com.datn.moneyai.models.entities.enums.UserRole;
import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
@EqualsAndHashCode(callSuper = true)
public class UserEntity extends BaseEntity{
    @Column(name = "username", unique = true, nullable = false)
    private String username;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "phone_number", unique = true)
    private String phoneNumber;

    @Column(name = "password", nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private UserRole role;

    @Column(name = "is_enabled")
    private Boolean isEnabled;

    @Column(name = "avatar_url")
    private String avatarUrl;
}
