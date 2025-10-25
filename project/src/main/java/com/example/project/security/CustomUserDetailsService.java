package com.example.project.security;

import com.example.project.model.User;
import com.example.project.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {   // 아이디로 유저 찾기
        System.out.println("Loading user by username: " + username);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        if (!user.isEnabled()) {  // 이메일 인증을 하지 않은 경우
            throw new UsernameNotFoundException("인증되지 않은 사용자입니다.");
        }
                
         // ✅ Spring Security의 UserDetails 객체 생성 (권한 추가)
        return new CustomUserDetails(user);
    }
}
