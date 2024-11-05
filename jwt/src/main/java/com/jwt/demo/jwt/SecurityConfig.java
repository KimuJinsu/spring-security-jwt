package com.jwt.demo.jwt;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import lombok.RequiredArgsConstructor;

/**
 * SecurityConfig 클래스는 Spring Security를 통해 애플리케이션 보안을 관리하는 설정 클래스입니다.
 * JWT 인증을 적용하고, 세션 없이 인증을 관리하며, 특정 URL에 접근 허용을 설정합니다.
 */
@Configuration
@EnableWebSecurity // Spring Security를 활성화합니다.
@RequiredArgsConstructor // final 필드에 대해 생성자를 자동으로 생성합니다.
public class SecurityConfig {

    private final TokenProvider tokenProvider; // JWT 토큰을 관리하는 TokenProvider 객체
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint; // 인증 실패 시 처리할 핸들러
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler; // 인가 실패 시 처리할 핸들러

    /**
     * 비밀번호를 암호화하기 위한 PasswordEncoder 빈을 생성합니다.
     * BCryptPasswordEncoder를 사용하여 암호를 해싱합니다.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * HttpSecurity 설정을 구성하는 메서드입니다.
     * JWT 필터를 추가하고, CSRF 보호를 비활성화하며, 세션을 사용하지 않도록 설정합니다.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        
        // CORS 및 CSRF 보호 비활성화
        http.cors(cors -> cors.disable());
        http.csrf(csrf -> csrf.disable());
        
        // H2 콘솔을 위한 헤더 설정: Frame-Options 비활성화
        http.headers(headers -> headers.frameOptions().disable());
        
        // 예외 처리 설정: 인증, 인가 예외 시 핸들러 지정
        http.exceptionHandling(
            e -> e.authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .accessDeniedHandler(jwtAccessDeniedHandler)
        );

        // 세션 관리 설정: STATELESS 설정을 통해 세션을 사용하지 않음
        http.sessionManagement(
            sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );

        // URL별 접근 권한 설정
        http.authorizeHttpRequests(
            c -> c.requestMatchers(new AntPathRequestMatcher("/api/login")).permitAll() // 로그인 API는 접근 허용
                .requestMatchers(new AntPathRequestMatcher("/api/refresh-token")).permitAll() // 토큰 갱신 API 허용
                .requestMatchers(new AntPathRequestMatcher("/api/signup")).permitAll() // 회원가입 API 허용
                .requestMatchers(new AntPathRequestMatcher("/favicon.ico")).permitAll() // 파비콘 허용
                .anyRequest().authenticated() // 그 외의 모든 요청은 인증 요구
        );

        // JWT 인증을 위한 JwtSecurityConfig 추가
        http.apply(new JwtSecurityConfig(tokenProvider));

        return http.build(); // SecurityFilterChain 반환
    }
}