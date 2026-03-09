package com.datn.moneyai.services.implement;

import com.datn.moneyai.models.dtos.auth.LoginGetResponse;
import com.datn.moneyai.models.dtos.auth.TokenResponse;
import com.datn.moneyai.models.entities.bases.User;
import com.datn.moneyai.models.global.ApiResult;
import com.datn.moneyai.models.security.JwtTokenProvider;
import com.datn.moneyai.models.security.UserPrincipal;
import com.datn.moneyai.repositories.UserRepository;
import com.datn.moneyai.services.interfaces.ITokenService;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import java.util.concurrent.TimeUnit;

@Service
@AllArgsConstructor
public class TokenService implements ITokenService {
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final StringRedisTemplate redisTemplate;

    /**
     * Tạo mới Access Token và Refresh Token cho người dùng dựa trên thông tin
     * UserDetails.
     * 
     * @param userDetails Thông tin chi tiết của người dùng (UserDetails).
     * @return ApiResult mang theo đối tượng TokenResponse chứa Access Token và
     *         Refresh Token.
     */
    @Override
    public ApiResult<TokenResponse> generateTokens(UserDetails userDetails) {
        String accessToken = jwtTokenProvider.generateAccessToken(userDetails);
        String refreshToken = jwtTokenProvider.generateRefreshToken(userDetails);
        long ttlSeconds = Math.max(0,
                (jwtTokenProvider.extractExpiration(refreshToken).getTime() - System.currentTimeMillis()) / 1000);
        if (ttlSeconds > 0) {
            String redisKey = "refreshToken:" + userDetails.getUsername();
            redisTemplate.opsForValue().set(redisKey, refreshToken, ttlSeconds, TimeUnit.SECONDS);
        }
        return ApiResult.success(TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtTokenProvider.extractExpiration(accessToken).getTime())
                .build(), "Tạo token thành công");
    }

    /**
     * Lấy thông tin người dùng hiện tại dựa trên Authentication và trả về dưới
     * dạng LoginGetResponse.
     *
     * @param authentication Thông tin xác thực của người dùng hiện tại.
     * @return ApiResult mang theo đối tượng LoginGetResponse chứa thông tin người
     *         dùng.
     */
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

    /**
     * Hàm phụ trợ để trích xuất userId từ Authentication.
     *
     * @param authentication Thông tin xác thực của người dùng hiện tại.
     * @return userId của người dùng.
     * @throws IllegalArgumentException nếu không thể lấy thông tin người dùng từ
     *                                  Authentication.
     */
    private Long getUserIdFromAuthentication(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserPrincipal userPrincipal) {
            return userPrincipal.getId();
        }
        throw new IllegalArgumentException("Không thể lấy thông tin người dùng từ Authentication");
    }
}