package com.jwt.demo.controller;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenResponse {
    private String accessToken;  // 클라이언트가 요청 시 사용할 액세스 토큰
    private String refreshToken; // 새 액세스 토큰을 발급받기 위한 리프레시 토큰
}