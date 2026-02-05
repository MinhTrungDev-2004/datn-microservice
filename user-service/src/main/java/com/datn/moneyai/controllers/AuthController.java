package com.datn.moneyai.controllers;

import com.datn.moneyai.models.dtos.auth.LoginRequest;
import com.datn.moneyai.models.dtos.auth.TokenResponse;
import com.datn.moneyai.models.dtos.users.UserCreateRequest;
import com.datn.moneyai.models.dtos.users.UserGetsResponse;
import com.datn.moneyai.models.global.ApiResult;
import com.datn.moneyai.services.interfaces.ITokenService;
import com.datn.moneyai.services.interfaces.IUserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/user")
public class AuthController extends ApiBaseController{
    private final AuthenticationManager authenticationManager;
    private final ITokenService tokenService;
    private final IUserService userService;

    // Constructor
    public AuthController(AuthenticationManager authenticationManager, ITokenService tokenService, IUserService userService) {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResult<UUID>> register(@RequestBody UserCreateRequest request) {
        return exeResponseEntity(() -> userService.createUser(request));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResult<TokenResponse>> login(@RequestBody LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            TokenResponse tokens = tokenService.generateTokens(userDetails);
            return ResponseEntity.ok(ApiResult.success(tokens, "Đăng Nhập Thành Công"));
        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResult.fail("Tên đăng nhập sai hoặc mật khẩu không chính xác"));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResult<List<UserGetsResponse>>> getUser() {
        ApiResult<List<UserGetsResponse>> result = userService.getUser();
        return ResponseEntity.ok(result);
    }
}
