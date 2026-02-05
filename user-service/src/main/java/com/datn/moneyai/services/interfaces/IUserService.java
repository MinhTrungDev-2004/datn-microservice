package com.datn.moneyai.services.interfaces;

import com.datn.moneyai.models.dtos.users.UserCreateRequest;
import com.datn.moneyai.models.dtos.users.UserGetsResponse;
import com.datn.moneyai.models.global.ApiResult;

import java.util.List;
import java.util.UUID;

public interface IUserService {
    ApiResult<List<UserGetsResponse>> getUser();
    ApiResult<UUID> createUser(UserCreateRequest request);
}
