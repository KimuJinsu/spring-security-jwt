package com.jwt.demo.jwt;

import java.io.IOException;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * JwtAccessDeniedHandler 클래스는 사용자가 권한이 없는 리소스에 접근하려고 할 때 발생하는 예외를 처리합니다.
 * Spring Security의 AccessDeniedHandler 인터페이스를 구현하여 접근 거부 시의 처리를 담당합니다.
 */
@Component // Spring이 관리하는 Bean으로 등록되며, 다른 클래스에서 주입하여 사용할 수 있습니다.
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    /**
     * handle 메서드는 사용자가 접근 권한이 없는 리소스에 접근할 때 호출됩니다.
     * HTTP 응답 코드로 403 FORBIDDEN을 반환하여 클라이언트에게 접근이 금지되었음을 알립니다.
     *
     * @param request 현재 요청 객체
     * @param response 현재 응답 객체
     * @param accessDeniedException 접근 거부 예외 객체
     * @throws IOException 입출력 예외가 발생할 수 있습니다.
     */
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
        // 접근이 거부된 경우, 클라이언트에 403 FORBIDDEN 상태 코드를 응답합니다.
        response.sendError(HttpServletResponse.SC_FORBIDDEN);
    }
}