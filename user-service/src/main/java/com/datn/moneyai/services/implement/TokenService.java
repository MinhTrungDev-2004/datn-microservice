package com.datn.moneyai.services.implement;

import com.datn.moneyai.models.dtos.auth.LoginGetResponse;
import com.datn.moneyai.models.dtos.auth.TokenResponse;
import com.datn.moneyai.models.entities.bases.User;
import com.datn.moneyai.models.global.ApiResult;
import com.datn.moneyai.models.security.JwtTokenProvider;
import com.datn.moneyai.models.security.UserPrincipal;
import com.datn.moneyai.repositories.UserRepository;
import com.datn.moneyai.services.interfaces.ITokenService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class TokenService implements ITokenService {
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    // Sử dụng StringRedisTemplate của Spring Boot để lưu token hợp lệ
    private final StringRedisTemplate redisTemplate;

    public TokenService(JwtTokenProvider jwtTokenProvider,
                        UserRepository userRepository,
                        StringRedisTemplate redisTemplate) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userRepository = userRepository;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public TokenResponse generateTokens(UserDetails userDetails) {
        // 1. Tạo Access Token và Refresh Token
        String accessToken = jwtTokenProvider.generateAccessToken(userDetails);
        String refreshToken = jwtTokenProvider.generateRefreshToken(userDetails);

        // 2. Tính toán thời gian sống (TTL) của Refresh Token
        long ttlSeconds = Math.max(0,
                (jwtTokenProvider.extractExpiration(refreshToken).getTime() - System.currentTimeMillis()) / 1000);

        // 3. LƯU REFRESH TOKEN VÀO REDIS (KEY HỢP LỆ)
        if (ttlSeconds > 0) {
            // Đặt tên Key là: refreshToken:{email_nguoi_dung}
            String redisKey = "refreshToken:" + userDetails.getUsername();

            // Lưu vào Redis với giá trị là token và thời gian hết hạn
            redisTemplate.opsForValue().set(redisKey, refreshToken, ttlSeconds, TimeUnit.SECONDS);
        }

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtTokenProvider.extractExpiration(accessToken).getTime())
                .build();
    }

    @Override
    public ApiResult<LoginGetResponse> getUserInfo(Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy người dùng với id: " + userId));

        LoginGetResponse response = LoginGetResponse.builder()
                .fullName(user.getFullName())
                .email(user.getEmail())
                .avatarUrl(user.getAvatarUrl())
                .role(user.getUserRoles().stream().findFirst().map(ur -> ur.getRole().getName().name()).orElse(null))
                .build();

        return ApiResult.success(response, "Lấy thông tin người dùng thành công");
    }

    private Long getUserIdFromAuthentication(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserPrincipal userPrincipal) {
            return userPrincipal.getId();
        }
        throw new IllegalArgumentException("Không thể lấy thông tin người dùng từ Authentication");
    }
}