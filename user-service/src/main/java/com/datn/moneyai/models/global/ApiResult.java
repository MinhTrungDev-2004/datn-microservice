package com.datn.moneyai.models.global;

import lombok.*;

@Getter
@Setter
public class ApiResult<T> {
    private Boolean status = false;

    // Thông báo cho user
    private String userMessage;

    // Mã định danh duy nhất cho mỗi yêu cầu, dùng để theo dõi và gỡ lỗi
    private String traceID = java.util.UUID.randomUUID().toString();

    // Thông báo nội bộ
    private String internalMessage;

    private T data;
    public static <T> ApiResult<T> success(T data, String userMessage) {
        ApiResult<T> result = new ApiResult<>();
        result.setStatus(true);
        result.setData(data);
        result.setUserMessage(userMessage);
        return result;
    }

    public static <T> ApiResult<T> fail(String userMessage) {
        ApiResult<T> result = new ApiResult<>();
        result.setStatus(false);
        result.setUserMessage(userMessage);
        return result;
    }
}
