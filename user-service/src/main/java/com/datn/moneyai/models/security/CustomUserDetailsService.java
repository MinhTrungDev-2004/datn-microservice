package com.datn.moneyai.models.security;

import com.datn.moneyai.models.entities.bases.User;
import com.datn.moneyai.repositories.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    /**
     * Tải thông tin người dùng dựa trên tên đăng nhập.
     *
     * @param username Tên đăng nhập của người dùng.
     * @return UserDetails chứa thông tin người dùng.
     * @throws UsernameNotFoundException Nếu không tìm thấy người dùng với tên đăng
     *                                   nhập đã cho.
     */
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        return new UserPrincipal(
                user.getId(),
                user.getEmail(),
                user.getPassword(),
                user.getUserRoles().stream()
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getRole().getName().name()))
                        .collect(Collectors.toList()));
    }
}
