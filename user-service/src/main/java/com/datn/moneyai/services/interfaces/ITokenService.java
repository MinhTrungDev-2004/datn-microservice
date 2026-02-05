package com.datn.moneyai.services.interfaces;

import com.datn.moneyai.models.dtos.auth.LoginGetResponse;
import com.datn.moneyai.models.dtos.auth.TokenResponse;
import com.datn.moneyai.models.global.ApiResult;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

public interface ITokenService {
    TokenResponse generateTokens(UserDetails userDetails);
    public ApiResult<LoginGetResponse> getUserInfo(Authentication authentication);
}
