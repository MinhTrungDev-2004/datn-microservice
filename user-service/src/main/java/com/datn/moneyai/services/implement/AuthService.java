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
    private final StringRedisTemplate redisTemplate;

    /**
     * Tạo mới một tài khoản người dùng hoặc quản trị viên (Admin).
     *
     * @param request Dữ liệu đầu vào chứa thông tin đăng ký (email, password,
     *                role).
     * @return ApiResult mang theo ID của người dùng vừa được tạo thành công.
     * @throws UserMessageException Nếu email đã tồn tại hoặc vai trò (role) không
     *                              hợp lệ.
     */
    @Override
    public ApiResult<Long> createUser(UserCreateRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserMessageException("Email đã tồn tại.");
        }
        String hashedPassword = passwordEncoder.encode(request.getPassword());

        User newUser = User.builder()
                .email(request.getEmail())
                .password(hashedPassword)
                .isActive(true)
                .build();

        Role userRoleEntity = roleRepository.findByName(RoleName.USER)
                .orElseThrow(() -> new UserMessageException("Lỗi hệ thống: Không tìm thấy quyền mặc định (USER)."));

        UserRole userRole = UserRole.builder()
                .user(newUser)
                .role(userRoleEntity)
                .build();
        newUser.setUserRoles(Set.of(userRole));

        userRepository.save(newUser);

        return ApiResult.success(newUser.getId(), "Đăng ký thành công.");
    }

    /**
     * Lấy danh sách những người dùng có quyền hệ thống (loại trừ các User thông
     * thường).
     * Kết quả trả về sẽ được sắp xếp theo thời gian cập nhật mới nhất.
     *
     * @return ApiResult chứa danh sách (List) các đối tượng UserGetsResponse.
     */
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
    /**
     * Xử lý đăng xuất người dùng bằng cách vô hiệu hóa Access Token và Refresh
     * Token.
     *
     * @param accessToken  Token truy cập hiện tại của người dùng.
     * @param refreshToken Token làm mới của người dùng cần được thu hồi.
     * @return ApiResult<Void> mang theo trạng thái và thông báo đăng xuất thành
     *         công.
     */
    public ApiResult<Void> logout(String accessToken, String refreshToken) {
        // 1. Đưa Access Token vào Blacklist
        if (accessToken != null && !accessToken.trim().isEmpty()) {
            blacklistToken(accessToken);
        }
        if (refreshToken != null && !refreshToken.trim().isEmpty()) {
            try {
                String username = jwtTokenProvider.extractUsername(refreshToken);
                String redisKey = "refreshToken:" + username;
                redisTemplate.delete(redisKey);
                log.info("Đã xóa Refresh Token hợp lệ của user: {}", username);
            } catch (Exception e) {
                log.warn("Không thể trích xuất username từ Refresh Token hoặc lỗi khi xóa Redis: {}", e.getMessage());
            }
            blacklistToken(refreshToken);
        }
        return ApiResult.success(null, "Đăng xuất thành công");
    }

    /**
     * Hàm phụ trợ tính toán thời gian sống còn lại và đưa Token vào Redis
     * (Blacklist).
     *
     * @param token Chuỗi token (Access Token hoặc Refresh Token) cần vô hiệu hóa.
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
            log.warn("Lỗi khi đưa token vào blacklist hoặc token đã hết hạn: {}", e.getMessage());
        }
    }
}