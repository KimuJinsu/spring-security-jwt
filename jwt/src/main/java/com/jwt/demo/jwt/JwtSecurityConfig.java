package com.jwt.demo.jwt;

import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import lombok.RequiredArgsConstructor;

/**
 * JwtSecurityConfig 클래스는 JWT 필터를 Spring Security의 필터 체인에 추가하는 설정을 제공합니다.
 * SecurityConfigurerAdapter를 상속받아 HttpSecurity 설정에 JWT 필터를 적용합니다.
 */
@RequiredArgsConstructor // final 필드를 인자로 받는 생성자를 자동으로 생성합니다.
public class JwtSecurityConfig extends 
    SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {
    
    private final TokenProvider tokenProvider; // JWT 토큰을 생성하고 인증 정보를 제공하는 TokenProvider 객체

    /**
     * HttpSecurity 설정에 JWT 필터를 추가하는 메서드입니다.
     * JWT 필터는 UsernamePasswordAuthenticationFilter 전에 실행되어 요청의 JWT 토큰을 확인합니다.
     * @param http HttpSecurity 객체로, 보안 설정을 적용할 수 있습니다.
     */
    @Override
    public void configure(HttpSecurity http) {
        // JwtFilter를 UsernamePasswordAuthenticationFilter 이전에 추가하여 인증 필터가 실행되기 전에 JWT 토큰을 확인하도록 합니다.
        http.addFilterBefore(
                new JwtFilter(tokenProvider),
                UsernamePasswordAuthenticationFilter.class
        );
    }
}