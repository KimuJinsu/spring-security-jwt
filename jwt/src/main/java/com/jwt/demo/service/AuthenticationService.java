package com.jwt.demo.service;

import java.time.LocalDateTime;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jwt.demo.controller.RefreshTokenRequest;
import com.jwt.demo.controller.TokenResponse;
import com.jwt.demo.dto.LoginDto;
import com.jwt.demo.dto.TokenDto;
import com.jwt.demo.entities.RefreshToken;
import com.jwt.demo.jwt.TokenProvider;
import com.jwt.demo.repository.RefreshTokenRepository;

/**
 * AuthenticationService 클래스는 JWT를 이용한 인증 처리를 수행하는 서비스입니다.
 * 로그인, 액세스 토큰 생성, 리프레시 토큰 관리 등의 기능을 제공합니다.
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class AuthenticationService {
	
    private final TokenProvider tokenProvider;  // JWT 토큰을 생성하고 유효성을 검사하는 클래스
    private final AuthenticationManagerBuilder authenticationManagerBuilder;  // Spring Security의 인증 관리자 빌더

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;  // 리프레시 토큰을 저장하는 저장소
	
    /**
     * 로그인 요청을 받아서 액세스 토큰과 리프레시 토큰을 생성합니다.
     * 
     * @param loginDto 사용자 인증 정보 (username, password)
     * @return 생성된 액세스 토큰과 리프레시 토큰을 담은 TokenResponse 객체
     */
	public Optional<TokenResponse> makeTokens(LoginDto loginDto) {
		log.info("makeTokens");

        // 사용자 이름과 비밀번호로 인증 토큰 생성
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword());

        // 인증 수행
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        log.info("username=" + authentication.getName());

        // 인증된 사용자 정보를 SecurityContext에 저장
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 액세스 토큰 생성
        String accessToken = tokenProvider.createToken(authentication, true);

        // 리프레시 토큰 생성 및 데이터베이스에 저장
        String refreshToken = tokenProvider.createAndPersistRefreshTokenForUser(authentication);

        // 토큰 응답 객체 생성
        TokenResponse tokenResponse = new TokenResponse(accessToken, refreshToken);

        // TokenResponse 객체를 Optional로 반환
        return Optional.ofNullable(tokenResponse);
	}

    /**
     * 유효한 리프레시 토큰을 사용하여 새 액세스 토큰을 생성합니다.
     * 
     * @param refreshTokenRequest 사용자로부터 받은 리프레시 토큰 요청
     * @param authentication 현재 인증 정보
     * @return 새로 발급된 액세스 토큰을 포함한 TokenDto 객체
     */
	@Transactional
	public Optional<TokenDto> makeNewAccessToken(RefreshTokenRequest refreshTokenRequest,
    		Authentication authentication) {
		String refreshTokenValue = refreshTokenRequest.getRefreshToken();		
    	
    	log.info("refreshToken from user. token value=" + refreshTokenValue);
    	
        // 데이터베이스에서 리프레시 토큰 조회
        RefreshToken validRefreshToken = 
        		refreshTokenRepository.findById(refreshTokenValue)
                .orElseThrow(() -> new IllegalStateException("Invalid refresh token"));

        TokenDto tokenDto = null;

        // 리프레시 토큰이 만료된 경우 삭제하고 null 반환
        if (isTokenExpired(validRefreshToken)) {
            refreshTokenRepository.delete(validRefreshToken);
            return Optional.ofNullable(tokenDto);
        }
        
        log.info("refreshToken from database. token value=" + validRefreshToken.getToken());
        
        // 새로운 액세스 토큰 생성
        String accessToken = tokenProvider.createToken(authentication, true);
        
        tokenDto = new TokenDto(accessToken);
        return Optional.ofNullable(tokenDto);
	}

    /**
     * 리프레시 토큰의 만료 여부를 확인합니다.
     * 
     * @param refreshToken 확인할 리프레시 토큰
     * @return 만료되었으면 true, 그렇지 않으면 false
     */
	public boolean isTokenExpired(RefreshToken refreshToken) {
        return refreshToken.getExpiryDate().isBefore(LocalDateTime.now());
    }
}