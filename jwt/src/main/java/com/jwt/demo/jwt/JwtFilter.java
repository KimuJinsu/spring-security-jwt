package com.jwt.demo.jwt;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * JwtFilter 클래스는 JWT 토큰의 유효성을 검사하고 인증 정보를 설정하는 필터입니다.
 * 모든 요청에 대해 이 필터를 거치며, JWT 토큰이 유효한 경우 SecurityContext에 인증 정보를 저장합니다.
 */
@Slf4j // 로깅을 위한 Lombok 어노테이션입니다.
@RequiredArgsConstructor // final 필드를 인자로 받는 생성자를 자동으로 생성합니다.
public class JwtFilter extends GenericFilterBean {

    public static final String AUTHORIZATION_HEADER = "Authorization"; // HTTP 헤더의 Authorization 키
    private final TokenProvider tokenProvider; // JWT 생성 및 인증 정보를 제공하는 TokenProvider 객체

    /**
     * 요청이 들어올 때마다 실행되는 메서드입니다.
     * JWT 토큰을 추출하고, 유효성을 검사한 후 SecurityContext에 인증 정보를 저장합니다.
     * @param servletRequest 클라이언트의 요청 객체
     * @param servletResponse 서버의 응답 객체
     * @param filterChain 필터 체인을 통해 다음 필터로 요청을 전달합니다.
     * @throws IOException 입출력 예외가 발생할 수 있습니다.
     * @throws ServletException 서블릿 예외가 발생할 수 있습니다.
     */
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        // HttpServletRequest로 변환하여 URI 및 JWT 토큰을 확인합니다.
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        String jwt = resolveToken(httpServletRequest); // 요청 헤더에서 JWT를 추출합니다.
        String requestURI = httpServletRequest.getRequestURI(); // 요청 URI를 가져옵니다.

        // JWT가 유효하면 인증 정보를 생성하고 SecurityContext에 저장합니다.
        if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
            Authentication authentication = tokenProvider.getAuthentication(jwt); // JWT로부터 인증 정보를 가져옵니다.
            SecurityContextHolder.getContext().setAuthentication(authentication); // SecurityContext에 인증 정보를 설정합니다.
            log.debug("Security Context에 '{}' 인증 정보를 저장했습니다, uri: {}", authentication.getName(), requestURI);
        } else {
            log.debug("유효한 JWT 토큰이 없습니다, uri: {}", requestURI);
        }

        // 다음 필터로 요청을 전달합니다.
        // 필터 체인은 여러 필터가 연속적으로 실행되는 구조로, 각 필터는 특정 작업을 수행한 후 요청을 다음 필터로 전달할지 결정합니다.
        filterChain.doFilter(servletRequest, servletResponse);
    }

    /**
     * Authorization 헤더에서 JWT를 추출하는 메서드입니다.
     * 헤더에 "Bearer "로 시작하는 토큰이 있으면 해당 부분을 제거하고 토큰만 반환합니다.
     * @param request 클라이언트의 HTTP 요청 객체
     * @return 추출한 JWT 토큰 문자열 또는 null
     */
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER); // Authorization 헤더에서 토큰을 추출합니다.

        // 토큰이 "Bearer "로 시작하면 앞부분을 제거하고 실제 토큰을 반환합니다.
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // "Bearer "를 제외한 순수 토큰을 반환합니다.
        }

        return null; // 유효하지 않은 경우 null 반환
    }
}