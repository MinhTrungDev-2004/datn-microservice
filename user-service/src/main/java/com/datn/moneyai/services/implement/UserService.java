package com.datn.moneyai.services.implement;

import com.datn.moneyai.exceptions.UserMessageException;
import com.datn.moneyai.models.dtos.users.UserCreateRequest;
import com.datn.moneyai.models.dtos.users.UserGetsResponse;
import com.datn.moneyai.models.entities.bases.UserEntity;
import com.datn.moneyai.models.entities.enums.UserRole;
import com.datn.moneyai.models.global.ApiResult;
import com.datn.moneyai.repositories.UserRepository;
import com.datn.moneyai.services.interfaces.IUserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
public class UserService implements IUserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public ApiResult<List<UserGetsResponse>> getUser() {
        List<UserEntity> users = userRepository.findAllByRoleNot((UserRole.USER));

        List<UserGetsResponse> responseList = users.stream()
                .map(user -> UserGetsResponse.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .role(user.getRole().name())
                        .createTime(user.getCreateAt())
                        .lastModifiedTime(user.getUpdatedAt())
                        .build())
                .sorted(Comparator.comparing(UserGetsResponse::getLastModifiedTime, Comparator.nullsLast(Comparator.reverseOrder())))
                .toList();

        return ApiResult.success(responseList, "Lấy danh sách người dùng thành công");
    }

    @Override
    public ApiResult<UUID> createUser(UserCreateRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UserMessageException("Tên đăng nhập đã tồn tại");
        }
        String hashedPassword = passwordEncoder.encode(request.getPassword());
        
        // Convert role từ String sang UserRole enum
        UserRole userRole;
        try {
            userRole = UserRole.valueOf(request.getRole().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new UserMessageException("Vai trò không hợp lệ. Vui lòng chọn USER hoặc ADMIN");
        }

        UserEntity newUser = UserEntity.builder()
                .username(request.getUsername())
                .password(hashedPassword)
                .role(userRole)
                .isEnabled(true)
                .build();
        userRepository.save(newUser);

        return ApiResult.success(newUser.getId(), "Đăng ký thành công");
    }
}
