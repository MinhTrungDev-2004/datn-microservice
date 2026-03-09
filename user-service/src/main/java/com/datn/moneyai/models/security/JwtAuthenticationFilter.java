package com.datn.moneyai.models.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenProvider tokenProvider;
    private final UserDetailsService userDetailsService;
    private static final List<String> EXCLUDED_PATHS = List.of(
            "/public/auth/register",
            "/public/auth/login",
            "/public/auth/logout");

    /**
     * Lọc JWT từ header Authorization và thiết lập Authentication trong
     * SecurityContext nếu token hợp lệ.
     *
     * @param request     Yêu cầu HTTP hiện tại.
     * @param response    Phản hồi HTTP hiện tại.
     * @param filterChain Chuỗi bộ lọc để tiếp tục xử lý yêu cầu.
     * @throws ServletException Nếu có lỗi trong quá trình xử lý bộ lọc.
     * @throws IOException      Nếu có lỗi I/O trong quá trình xử lý bộ lọc.
     */
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String path = request.getServletPath();

        // Bỏ qua kiểm tra nếu path khớp với các đường public
        if (shouldSkipFilter(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7);
        username = tokenProvider.extractUsername(jwt);

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
            if (tokenProvider.validateToken(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Kiểm tra xem đường dẫn có thuộc danh sách các đường dẫn cần bỏ qua hay không.
     * 
     * @param path Đường dẫn của yêu cầu hiện tại.
     * @return true nếu đường dẫn nên bỏ qua, false nếu không nên bỏ qua.
     */
    private boolean shouldSkipFilter(String path) {
        return EXCLUDED_PATHS.stream().anyMatch(path::startsWith);
    }
}
