package com.jwt.demo.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import com.jwt.demo.dto.LoginDto;
import com.jwt.demo.dto.LogoutDto;
import com.jwt.demo.dto.TokenDto;
import com.jwt.demo.entities.RefreshToken;
import com.jwt.demo.jwt.JwtFilter;
import com.jwt.demo.jwt.TokenProvider;
import com.jwt.demo.repository.RefreshTokenRepository;
import com.jwt.demo.service.AuthenticationService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j // 로깅을 위한 애노테이션
@RestController // REST API 요청을 처리하는 컨트롤러 클래스임을 선언
@RequestMapping("/api") // 이 컨트롤러의 공통 요청 경로 설정
@RequiredArgsConstructor // final 필드에 대한 생성자 자동 생성 (DI를 위한 필드)
public class AuthController {  //이 컨트롤러는 로그인과 토큰 갱신 같은 인증 관련 작업을 처리하는 역할
   
    private final AuthenticationService authenticationService; // 인증 관련 서비스를 의존성 주입
    private final RefreshTokenRepository refreshTokenRepository; // 주입을 위한 필드 추가

    /**
     * 로그인 요청을 처리하는 메소드
     * @param loginDto 클라이언트에서 받은 로그인 정보 (ID, 비밀번호)
     * @return 엑세스 및 리프레시 토큰을 포함한 응답
     */
    @PostMapping("/login") // /api/login 경로로 POST 요청을 처리
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginDto loginDto) {

        // loginDto의 로그인 정보를 이용해 토큰을 생성
        Optional<TokenResponse> optTokenResponse = 
                authenticationService.makeTokens(loginDto);
        
        // HttpHeaders를 생성하고, Authorization 헤더에 Bearer + AccessToken을 추가
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(JwtFilter.AUTHORIZATION_HEADER, "Bearer " + 
                optTokenResponse.get().getAccessToken());
        
        // 생성된 토큰을 포함한 응답을 반환
        ResponseEntity<TokenResponse> ret = new ResponseEntity<>(
                optTokenResponse.get(), 
                httpHeaders, 
                HttpStatus.OK);
        
        return ret; // 응답 반환
    } 
    
    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestBody LogoutDto logoutDto) {
        // refreshTokenRepository 인스턴스를 사용하여 리프레시 토큰을 조회
        Optional<RefreshToken> refreshToken = refreshTokenRepository.findByToken(logoutDto.getRefreshToken());
        
        if (refreshToken.isPresent()) {
            refreshTokenRepository.delete(refreshToken.get()); // DB에서 토큰 삭제
            return ResponseEntity.ok("Successfully logged out");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid refresh token");
        }
    }
    
    /**
     * 리프레시 토큰을 사용해 새로운 엑세스 토큰을 발급하는 메소드
     * @param refreshTokenRequest 리프레시 토큰 요청 정보 (리프레시 토큰 값 포함)
     * @param authentication 현재 인증 정보
     * @return 새롭게 발급된 엑세스 토큰 또는 에러 메시지
     */
    @PostMapping("/refresh-token") // /api/refresh-token 경로로 POST 요청을 처리
    public ResponseEntity<?> refreshToken(@RequestBody 
            RefreshTokenRequest refreshTokenRequest,
            Authentication authentication) {      
        
        try {
            // 리프레시 토큰을 이용해 새로운 엑세스 토큰을 발급 요청
            Optional<TokenDto> tokenDto = 
                    authenticationService.makeNewAccessToken(
                            refreshTokenRequest, authentication);
            
            // 토큰 생성 성공 시 새 엑세스 토큰 반환
            if (!tokenDto.isEmpty()) {
                return ResponseEntity.ok(tokenDto.get());
            } else {
                // 리프레시 토큰 만료 시 400 Bad Request 상태 코드를 반환, 오류 메시지 반환
                return ResponseEntity.badRequest().body("Refresh token expired. Please login again.");
            }            
        
        } catch (Exception e) {
            // 예외 발생 시 에러 메시지 반환
            return ResponseEntity.badRequest().body(e.getMessage());
        }        
    }
    
}