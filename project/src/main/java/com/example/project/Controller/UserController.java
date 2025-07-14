package com.example.project.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.BindingResult;
import com.example.project.Security.CustomUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.example.project.DTO.RegisterDTO;
import com.example.project.DTO.UserDTO;
import com.example.project.Service.UserService;
import jakarta.validation.Valid;
import com.example.project.Model.User;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;


@RestController
@RequiredArgsConstructor
public class UserController {
  private final UserService userService;

  @PostMapping("/register")	// 유저 등록
  public ResponseEntity<?> Register(@RequestBody @Valid RegisterDTO RegisterDTO, BindingResult result) {
    if (result.hasErrors()) {  // 유효성 검사 실패
        return ResponseEntity.badRequest().body(result.getAllErrors());
    }
    try {
        userService.saveUser(RegisterDTO);	// 유저 등록
    } catch (Exception e) {   // 예외 처리
        return ResponseEntity.badRequest().body("회원가입 실패: " + e.getMessage());
    }
    return ResponseEntity.ok("회원가입 성공");
  }

  @GetMapping("/admin/userlist")	// 유저 리스트 불러오기
  public ResponseEntity<List<User>> ShowUserList(@AuthenticationPrincipal CustomUserDetails principal) {
    if (principal == null || !principal.hasRole("ROLE_ADMIN")) {  // 로그인 안했거나 관리자 권한이 없는 경우
        return ResponseEntity.status(403).body(null);  // 권한 없음
    }
    List<User> userList = userService.getUserList();
    return ResponseEntity.ok(userList);
  }

  @PostMapping("/user/name")	// 유저 수정 (이름)
  public ResponseEntity<?> EditUsername(@AuthenticationPrincipal CustomUserDetails principal,
                             @RequestParam("newName") String newName) {
    try {
        Long userId = principal.getUserId();
        userService.updateName(userId, newName);
    } catch (IllegalArgumentException e) {   // 예외 처리
        return ResponseEntity.badRequest().body("유저 이름 수정 실패: " + e.getMessage());
    }
    return ResponseEntity.ok("유저 이름 수정 성공");
  }

  @DeleteMapping("/user")    // 유저 삭제
  public ResponseEntity<?> DeleteUser(@AuthenticationPrincipal CustomUserDetails principal) {
    try {
        Long userId = principal.getUserId();
        userService.deleteUser(userId);  // 유저 삭제
    } catch (IllegalArgumentException e) {   // 예외 처리
        return ResponseEntity.badRequest().body("유저 삭제 실패: " + e.getMessage());
    }
    return ResponseEntity.ok("유저 삭제 성공");
  }

  @GetMapping("/auth/user")   // 현재 로그인한 유저 정보 조회
  public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal CustomUserDetails principal) {
    if (principal == null) {
        return ResponseEntity.status(401).body("Unauthorized");
    }
    User user = principal.getUser();
    UserDTO dto = new UserDTO(user.getId(), user.getUsername(), user.getName(),
                               user.getEmail(), user.getPhoneNumber(), user.getAddress());
    return ResponseEntity.ok(dto);
  }

  @GetMapping("/auth/role")   // 현재 로그인한 유저의 권한 조회
  public ResponseEntity<?> getCurrentUserRole(@AuthenticationPrincipal CustomUserDetails principal) {
    String role = principal.getAuthorities().stream()
        .findFirst()
        .map(GrantedAuthority::getAuthority)
        .orElse("USER");
    return ResponseEntity.ok(role);
  }
}