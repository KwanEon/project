package com.example.project;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.Mock;
import org.mockito.InjectMocks;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.example.project.service.UserService;
import com.example.project.service.EmailService;
import com.example.project.repository.UserRepository;
import com.example.project.dto.RegisterDTO;
import com.example.project.model.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@ExtendWith(MockitoExtension.class)
public class UnitTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("회원가입 성공")
    public void 회원가입_성공() throws Exception {
        // given
        RegisterDTO userDto = RegisterDTO.builder()
                .username("testuser")
                .name("홍길동")
                .password("password123")
                .email("email@email.com")
                .phoneNumber("01012345678")
                .build();

        // Mocking
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByPhoneNumber(anyString())).thenReturn(false);
        when(bCryptPasswordEncoder.encode(anyString())).thenReturn("encodedPassword");

        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);  // 전달된 User 객체 가져오기
            user.setId(1L); // 저장 후 ID 설정
            return user;
        });

        // when
        Long savedId = userService.saveUser(userDto);

        // then
        assertNotNull(savedId);
        assertEquals(1L, savedId); // 반환된 ID가 예상과 같은지 확인

        // verify
        verify(userRepository, times(1)).existsByUsername("testuser");
        verify(userRepository, times(1)).existsByEmail("email@email.com");
        verify(userRepository, times(1)).existsByPhoneNumber("01012345678");
        verify(bCryptPasswordEncoder, times(1)).encode("password123");
        verify(userRepository, times(1)).save(any(User.class));
        verify(emailService, times(1)).sendVerificationEmail(eq("email@email.com"), contains("/auth/verify?token="));
    }

    @Test
    public void 회원가입_중복회원예외() {
        // given
        RegisterDTO userDto = RegisterDTO.builder()
                .username("testuser")
                .name("홍길동")
                .password("password123")
                .email("email@email.com")
                .phoneNumber("01012345678")
                .build();

        // Mocking: username이 이미 존재하는 경우
        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        // when & then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.saveUser(userDto);
        });

        assertEquals("이미 등록된 아이디입니다.", exception.getMessage());

        // save 메소드는 호출되지 않아야 함
        verify(bCryptPasswordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
        verify(emailService, never()).sendVerificationEmail(anyString(), anyString());
    }
}
