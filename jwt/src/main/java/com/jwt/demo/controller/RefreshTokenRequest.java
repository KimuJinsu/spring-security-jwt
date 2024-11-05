package com.jwt.demo.controller;

import lombok.*;

// Lombok 애노테이션을 사용해 Getter와 Setter 메소드 자동 생성
@Getter
@Setter
public class RefreshTokenRequest {
    private String refreshToken; // 클라이언트로부터 전달받는 리프레시 토큰을 저장하는 필드
}