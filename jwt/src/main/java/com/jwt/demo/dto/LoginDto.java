package com.jwt.demo.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor     // 데이터를 전달하기 위한 객체 DTO
public class LoginDto {

    // 사용자 이름 필드, null이 아니며 3자 이상 50자 이하로 제한
    @NotNull
    @Size(min = 3, max = 50)
    private String username;

    // 비밀번호 필드, null이 아니며 3자 이상 100자 이하로 제한
    @NotNull
    @Size(min = 3, max = 100)
    private String password;
}