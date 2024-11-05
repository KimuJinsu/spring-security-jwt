package com.jwt.demo.jwt;

import java.io.IOException;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * JwtAuthenticationEntryPoint 클래스는 인증되지 않은 사용자가 보호된 리소스에 접근하려고 할 때 발생하는 예외를 처리합니다.
 * Spring Security의 AuthenticationEntryPoint 인터페이스를 구현하여 인증 실패 시의 처리를 담당합니다.
 */
@Component // Spring이 관리하는 Bean으로 등록하여 다른 클래스에서 주입해 사용할 수 있습니다.
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    /**
     * commence 메서드는 인증되지 않은 사용자가 보호된 리소스에 접근할 때 호출됩니다.
     * HTTP 응답 코드로 401 UNAUTHORIZED를 반환하여 클라이언트에게 인증이 필요함을 알립니다.
     *
     * @param request 현재 요청 객체
     * @param response 현재 응답 객체
     * @param authException 인증 예외 객체
     * @throws IOException 입출력 예외가 발생할 수 있습니다.
     */
    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        // 인증이 필요한 리소스에 접근할 때, 클라이언트에 401 UNAUTHORIZED 상태 코드를 응답합니다.
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
    }
}