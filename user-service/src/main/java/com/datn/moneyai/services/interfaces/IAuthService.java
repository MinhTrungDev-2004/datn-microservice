package com.datn.moneyai.services.interfaces;

import com.datn.moneyai.models.dtos.users.UserCreateRequest;
import com.datn.moneyai.models.dtos.users.UserGetsResponse;
import com.datn.moneyai.models.global.ApiResult;

import java.util.List;

public interface IAuthService {
    ApiResult<Long> createUser(UserCreateRequest request);

    ApiResult<List<UserGetsResponse>> getUser();

    ApiResult<Void> logout(String accessToken, String refreshToken);
}
