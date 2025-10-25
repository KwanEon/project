package com.example.project.security;
import lombok.Getter;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.project.model.User;

import java.util.Collections;
import java.util.Collection;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@Getter
public class CustomUserDetails implements UserDetails {
    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    public Long getUserId() {   // 유저 ID 반환
        return user.getId();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {    // 권한 반환 (ROLE_USER, ROLE_ADMIN 등)
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
    }

    @Override
    public String getPassword() {   // 비밀번호 반환
        return user.getPassword();
    }

    @Override
    public String getUsername() {   // 아이디 반환
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {  // 계정 만료 여부
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {   // 계정 잠금 여부   
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {  // 비밀번호 만료 여부
        return true;
    }

    @Override
    public boolean isEnabled() {    // 계정 활성화 여부
        return true;
    }

    public boolean hasRole(String role) {
        if (getAuthorities() == null) return false;
        for (GrantedAuthority authority : getAuthorities()) {
            if (authority.getAuthority().equals(role)) {
                return true;
            }
        }
        return false;
    }
}
