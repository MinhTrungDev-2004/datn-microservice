package com.datn.moneyai.models.redis;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenBlackList {
    private String sessionId;
    private long expirationSeconds;
}
