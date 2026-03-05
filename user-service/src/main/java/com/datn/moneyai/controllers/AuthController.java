package com.datn.moneyai.controllers;

import com.datn.moneyai.models.dtos.auth.LoginRequest;
import com.datn.moneyai.models.dtos.auth.TokenResponse;
import com.datn.moneyai.models.dtos.users.UserCreateRequest;
import com.datn.moneyai.models.global.ApiResult;
import com.datn.moneyai.models.dtos.auth.LoginGetResponse;
import com.datn.moneyai.services.interfaces.ITokenService;
import com.datn.moneyai.services.interfaces.IAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AuthController extends ApiBaseController {
    private final AuthenticationManager authenticationManager;
    private final ITokenService tokenService;
    private final IAuthService authService;


    /**
     * API tạo mới người dùng
     */
    @PostMapping("/public/auth/register")
    public ResponseEntity<ApiResult<Long>> register(@RequestBody UserCreateRequest request) {
        return exeResponseEntity(() -> authService.createUser(request));
    }

    /**
     * API login
     */
    @PostMapping("/public/auth/login")
    public ResponseEntity<ApiResult<TokenResponse>> login(@RequestBody LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()));
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            TokenResponse tokens = tokenService.generateTokens(userDetails);
            ResponseCookie cookie = ResponseCookie.from("refreshToken", tokens.getRefreshToken())
                    .httpOnly(true)
                    .secure(false)
                    .path("/")
                    .maxAge(7 * 24 * 60 * 60)
                    .sameSite("Strict")
                    .build();
            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, cookie.toString())
                    .body(ApiResult.success(tokens, "Đăng nhập thành công"));
        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResult.fail("Email hoặc mật khẩu không chính xác"));
        }
    }

    /**
     * API lấy thông tin người dùng đăng nhập
     */
    @GetMapping("/auth/get-info")
    public ResponseEntity<ApiResult<LoginGetResponse>> getCurrentUser(Authentication authentication) {
        ApiResult<LoginGetResponse> result = tokenService.getUserInfo(authentication);
        return ResponseEntity.ok(result);
    }

    /**
     * API logout
     */
    @DeleteMapping("/public/auth/logout")
    public ResponseEntity<ApiResult<Void>> logout(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authHeader,
            @CookieValue(name = "refreshToken", required = false) String refreshToken) {
        String accessToken = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            accessToken = authHeader.substring(7);
        }
        authService.logout(accessToken, refreshToken);
        ResponseCookie clearCookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .build();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, clearCookie.toString())
                .body(ApiResult.success(null, "Đăng xuất thành công"));
    }
}
