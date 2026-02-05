package com.datn.moneyai.services.implement;

import com.datn.moneyai.models.dtos.auth.LoginGetResponse;
import com.datn.moneyai.models.dtos.auth.TokenResponse;
import com.datn.moneyai.models.entities.bases.UserEntity;
import com.datn.moneyai.models.global.ApiResult;
import com.datn.moneyai.models.security.JwtTokenProvider;
import com.datn.moneyai.models.security.UserPrincipal;
import com.datn.moneyai.repositories.UserRepository;
import com.datn.moneyai.services.interfaces.ITokenService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class TokenService implements ITokenService {
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    public TokenService(JwtTokenProvider jwtTokenProvider, UserRepository userRepository) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userRepository = userRepository;
    }

    @Override
    public TokenResponse generateTokens(UserDetails userDetails) {
        String accessToken = jwtTokenProvider.generateAccessToken(userDetails);
        String refreshToken = jwtTokenProvider.generateRefreshToken(userDetails);

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtTokenProvider.extractExpiration(accessToken).getTime())
                .build();
    }

    @Override
    public ApiResult<LoginGetResponse> getUserInfo(Authentication authentication) {
        UUID userId = getUserIdFromAuthentication(authentication);

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy người dùng với id: " + userId));

        LoginGetResponse response = LoginGetResponse.builder()
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .role(user.getRole().name())
                .build();

        return ApiResult.success(response, "Lấy thông tin người dùng thành công");
    }

    private UUID getUserIdFromAuthentication(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserPrincipal userPrincipal) {
            return userPrincipal.getId();
        }
        throw new IllegalArgumentException("Không thể lấy thông tin người dùng từ Authentication");
    }

}
