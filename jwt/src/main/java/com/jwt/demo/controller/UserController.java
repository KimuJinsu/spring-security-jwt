package com.jwt.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.DispatcherServlet;
import com.jwt.demo.dto.UserDto;
import com.jwt.demo.entities.User;
import com.jwt.demo.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {
    private final UserService userService;
    
    // 기타 HandlerMethodArgumentResolver와 DispatcherServlet (불필요한 의존성 제거 필요)
    
//    private HandlerMethodArgumentResolver xxx;
//    
//    private RequestParamMethodArgumentResolver uyyy;
//    
//    private DispatcherServlet dps;
    
    // 회원가입 요청을 처리하는 메서드
    @PostMapping("/signup")              //  클라이언트가 전송한 JSON 데이터를 UserDto 객체로 변환해 signup 메서드에 전달
    public ResponseEntity<User> signup(@Valid @RequestBody UserDto userDto) {
        return ResponseEntity.ok(userService.signup(userDto));
    }
    
  
    // 사용자 본인의 정보 조회 (USER 또는 ADMIN 권한 필요)
    @GetMapping("/user")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<User> getMyUserInfo() {
        return ResponseEntity.ok(userService.getMyUserWithAuthorities().orElse(null));
    }

    // 특정 사용자의 정보 조회 (ADMIN 권한 필요)
    @GetMapping("/user/{username}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<User> getUserInfo(@PathVariable String username) {
        return ResponseEntity.ok(userService.getUserWithAuthorities(username).orElse(null));
    }
}