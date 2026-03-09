package com.datn.moneyai.models.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtTokenProvider {
    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    @Value("${jwt.refresh-expiration}")
    private Long refreshExpiration;

    private Key getSigningKey() {
        byte[] keyBytes = secret.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Tạo token JWT cho người dùng dựa trên thông tin chi tiết của người dùng.
     * 
     * @param userDetails Thông tin chi tiết của người dùng (UserDetails).
     * @return Token JWT được tạo ra dưới dạng chuỗi.
     */
    public String generateAccessToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        if (userDetails instanceof UserPrincipal) {
            claims.put("uid", ((UserPrincipal) userDetails).getId());
        }
        return createToken(claims, userDetails.getUsername(), expiration);
    }

    /**
     * Tạo token làm mới (Refresh Token) cho người dùng dựa trên thông tin chi tiết
     * của người dùng.
     * 
     * @param userDetails Thông tin chi tiết của người dùng (UserDetails).
     * @return Refresh Token được tạo ra dưới dạng chuỗi.
     */
    public String generateRefreshToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        if (userDetails instanceof UserPrincipal) {
            claims.put("uid", ((UserPrincipal) userDetails).getId());
        }
        return createToken(claims, userDetails.getUsername(), refreshExpiration);
    }

    /**
     * Tạo token JWT với các claims, subject và thời gian hết hạn được chỉ định.
     * 
     * @param claims     Các claims bổ sung để đưa vào token (ví dụ: user ID).
     * @param subject    Tên đăng nhập của người dùng (subject).
     * @param expiration Thời gian hết hạn của token tính bằng milliseconds.
     * @return Token JWT được tạo ra dưới dạng chuỗi.
     */
    private String createToken(Map<String, Object> claims, String subject, Long expiration) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Xác thực token JWT bằng cách kiểm tra chữ ký và thời gian hết hạn.
     * 
     * @param token       Token JWT cần được xác thực.
     * @param userDetails Thông tin chi tiết của người dùng để so sánh với thông
     *                    tin trong token.
     * @return true nếu token hợp lệ và khớp với thông tin người dùng, false nếu
     *         không hợp lệ hoặc có lỗi trong quá trình xác thực.
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        try {
            Claims claims = extractAllClaims(token);
            String username = claims.getSubject();
            return (username.equals(userDetails.getUsername()));

        } catch (SignatureException e) {
            logger.error("Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }

    /**
     * Trích xuất tên đăng nhập (username) từ token JWT.
     * 
     * @param token Token JWT cần trích xuất thông tin.
     * @return Tên đăng nhập được trích xuất từ token, hoặc null nếu không thể
     *         trích xuất.
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Trích xuất userId từ token JWT.
     * 
     * @param token Token JWT cần trích xuất thông tin.
     * @return userId được trích xuất từ token, hoặc null nếu không thể trích xuất.
     */
    public Long extractUserId(String token) {
        Number id = extractClaim(token, claims -> claims.get("uid", Number.class));
        return id == null ? null : id.longValue();
    }

    /**
     * Trích xuất thời gian hết hạn của token JWT.
     * 
     * @param token Token JWT cần trích xuất thông tin.
     * @return Thời gian hết hạn của token dưới dạng Date, hoặc null nếu không thể
     *         trích xuất.
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Trích xuất một claim cụ thể từ token JWT bằng cách sử dụng một hàm resolver.
     * 
     * @param <T>            Kiểu dữ liệu của claim cần trích xuất.
     * @param token          Token JWT cần trích xuất thông tin.
     * @param claimsResolver Hàm resolver để trích xuất claim từ Claims object.
     * @return Giá trị của claim được trích xuất, hoặc null nếu không thể trích
     *         xuất.
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Trích xuất tất cả claims từ token JWT.
     * 
     * @param token Token JWT cần trích xuất thông tin.
     * @return Claims object chứa tất cả claims được trích xuất từ token.
     * @throws JwtException nếu có lỗi trong quá trình phân tích token (ví dụ: token
     *                      không hợp lệ, hết hạn, v.v.).
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
