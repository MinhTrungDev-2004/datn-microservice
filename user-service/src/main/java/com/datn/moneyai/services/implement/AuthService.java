package com.datn.moneyai.services.implement;

import com.datn.moneyai.exceptions.UserMessageException;
import com.datn.moneyai.models.dtos.users.UserCreateRequest;
import com.datn.moneyai.models.dtos.users.UserGetsResponse;
import com.datn.moneyai.models.entities.bases.Role;
import com.datn.moneyai.models.entities.bases.User;
import com.datn.moneyai.models.entities.bases.UserRole;
import com.datn.moneyai.models.entities.enums.RoleName;
import com.datn.moneyai.models.global.ApiResult;
import com.datn.moneyai.models.redis.TokenBlackList;
import com.datn.moneyai.models.redis.TokenBlackListRepository;
import com.datn.moneyai.models.security.JwtTokenProvider;
import com.datn.moneyai.repositories.RoleRepository;
import com.datn.moneyai.repositories.UserRepository;
import com.datn.moneyai.services.interfaces.IAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService implements IAuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final TokenBlackListRepository tokenBlackListRepository;

    // Bổ sung StringRedisTemplate để xóa token hợp lệ
    private final StringRedisTemplate redisTemplate;

    @Override
    public ApiResult<Long> createUser(UserCreateRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserMessageException("Email đã tồn tại.");
        }
        String hashedPassword = passwordEncoder.encode(request.getPassword());

        RoleName roleName;
        try {
            roleName = RoleName.valueOf(request.getRole().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new UserMessageException("Vai trò không hợp lệ. Vui lòng chọn USER hoặc ADMIN");
        }

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new UserMessageException("Vai trò không hợp lệ."));

        User newUser = User.builder()
                .email(request.getEmail())
                .password(hashedPassword)
                .isActive(true)
                .build();

        UserRole userRole = UserRole.builder()
                .user(newUser)
                .role(role)
                .build();
        newUser.setUserRoles(Set.of(userRole));

        userRepository.save(newUser);

        return ApiResult.success(newUser.getId(), "Đăng ký thành công.");
    }

    @Override
    public ApiResult<List<UserGetsResponse>> getUser() {
        List<User> users = userRepository.findByUserRoles_Role_NameNot(RoleName.USER);

        List<UserGetsResponse> responseList = users.stream()
                .map(user -> UserGetsResponse.builder()
                        .id(user.getId())
                        .email(user.getEmail())
                        .role(user.getUserRoles().stream().findFirst().map(ur -> ur.getRole().getName().name())
                                .orElse(null))
                        .avatarUrl(user.getAvatarUrl())
                        .defaultCurrency(user.getDefaultCurrency())
                        .createTime(user.getCreatedAt())
                        .lastModifiedTime(user.getUpdatedAt())
                        .build())
                .sorted(Comparator.comparing(UserGetsResponse::getLastModifiedTime,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .toList();

        return ApiResult.success(responseList, "Lấy danh sách người dùng thành công.");
    }

    @Override
    public void logout(String accessToken, String refreshToken) {
        // 1. Đưa Access Token vào Blacklist
        if (accessToken != null && !accessToken.trim().isEmpty()) {
            blacklistToken(accessToken);
        }

        // 2. Xử lý Refresh Token: Xóa khỏi danh sách hợp lệ và đưa vào Blacklist
        if (refreshToken != null && !refreshToken.trim().isEmpty()) {
            try {
                // Lấy email (username) từ token để tìm đúng Key trong Redis
                String username = jwtTokenProvider.extractUsername(refreshToken);
                String redisKey = "refreshToken:" + username;

                // Xóa Refresh Token hợp lệ khỏi Redis
                redisTemplate.delete(redisKey);
                log.info("Đã xóa Refresh Token hợp lệ của user: {}", username);
            } catch (Exception e) {
                log.warn("Không thể trích xuất username từ Refresh Token hoặc lỗi khi xóa Redis: {}", e.getMessage());
            }

            // Đưa Refresh Token này vào Blacklist để đề phòng bị sử dụng lại
            blacklistToken(refreshToken);
        }
    }

    /**
     * Hàm dùng chung để tính toán thời gian và đưa Token vào Redis (Blacklist)
     */
    private void blacklistToken(String token) {
        try {
            // Lấy thời gian hết hạn của token
            long expirationSeconds = Math.max(0,
                    (jwtTokenProvider.extractExpiration(token).getTime() - System.currentTimeMillis()) / 1000);

            // Nếu token còn hạn thì mới cần cho vào blacklist
            if (expirationSeconds > 0) {
                TokenBlackList blackListRecord = new TokenBlackList(token, expirationSeconds);
                tokenBlackListRepository.put(blackListRecord);
                log.info("Đã đưa token vào blacklist với thời gian sống (giây): {}", expirationSeconds);
            }
        } catch (Exception e) {
            // Nếu token đã hết hạn sẵn hoặc không parse được, ta bỏ qua không cần đưa vào Blacklist nữa
            log.warn("Lỗi khi đưa token vào blacklist hoặc token đã hết hạn: {}", e.getMessage());
        }
    }
}