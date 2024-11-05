package com.jwt.demo.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * GlobalExceptionHandler는 애플리케이션의 전역 예외 처리를 담당하는 클래스입니다.
 * @ControllerAdvice 애노테이션을 통해 모든 컨트롤러에서 발생하는 예외를 처리할 수 있습니다.
 */
@ControllerAdvice // 전역 예외 처리를 활성화하는 애노테이션
public class GlobalExceptionHandler {

    /**
     * handleException 메서드는 Exception 클래스의 예외를 처리합니다.
     * 발생한 예외의 메시지를 클라이언트에게 500 (INTERNAL_SERVER_ERROR) 상태 코드와 함께 반환합니다.
     * @param e 처리할 예외 객체
     * @return 예외 메시지를 포함한 ResponseEntity 객체
     */
    @ExceptionHandler(Exception.class) // Exception 타입의 예외가 발생할 때 이 메서드가 호출됩니다.
    public ResponseEntity<String> handleException(Exception e) {
        // HTTP 상태 코드 500 (서버 내부 오류)과 예외 메시지를 클라이언트에 반환
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
    }
}