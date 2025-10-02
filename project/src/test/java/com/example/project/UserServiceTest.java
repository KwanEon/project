package com.example.project;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import com.example.project.service.UserService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.project.dto.RegisterDTO;
import com.example.project.model.User;

@SpringBootTest
@Transactional
public class UserServiceTest {

    @Autowired
    private UserService userService;
    
    @Test
	public void 회원가입() throws Exception{
		//given
		RegisterDTO user = RegisterDTO.builder()
				.username("testuser")
				.name("홍길동")
				.password("password123")
				.email("email@email.com")
				.phoneNumber("01012345678")
				.build();

		//when
		Long savedId = userService.saveUser(user);

		//then
		User savedUser = userService.findUserById(savedId);
		System.out.println("savedUser.id = " + savedUser.getId());
		assertNotNull(savedUser);
		assertEquals("testuser", savedUser.getUsername());
		assertEquals("홍길동", savedUser.getName());
	}

	@Test
	public void 중복_회원_예외() throws Exception{
		//given
		RegisterDTO user1 = RegisterDTO.builder()
				.username("testuser")
				.name("홍길동")
				.password("password123")
				.email("email@email.com")
				.phoneNumber("01012345678")
				.build();

		RegisterDTO user2 = RegisterDTO.builder()
				.username("testuser")
				.name("김철수")
				.password("password456")
				.email("email2@email.com")
				.phoneNumber("01087654321")
				.build();

		//when
		userService.saveUser(user1);

		//then
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
        userService.saveUser(user2);
    	});
    	assertEquals("이미 등록된 아이디입니다.", exception.getMessage());
	}
}
