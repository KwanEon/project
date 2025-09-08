package com.example.project;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.context.ActiveProfiles;
import com.example.project.dto.RegisterDTO;
import com.example.project.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ProjectApplicationTests {
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
		System.out.println("저장된 사용자 ID: " + savedId);

		//then
		var savedUser = userService.findUserById(savedId);
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
				.email("email@email.com")
				.phoneNumber("01087654321")
				.build();

		//when
		userService.saveUser(user1);

		//then
		assertThrows(IllegalArgumentException.class, () -> {
			userService.saveUser(user2);
		});
	}
}
