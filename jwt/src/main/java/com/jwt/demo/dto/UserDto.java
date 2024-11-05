package com.jwt.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

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
@NoArgsConstructor
public class UserDto {

    // 사용자의 아이디를 나타내며 최소 3자 이상 50자 이하로 제한
    @NotNull
    @Size(min = 3, max = 50)
    private String username;

    // 사용자의 비밀번호를 나타내며 클라이언트에서만 작성 가능하게 설정
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotNull
    @Size(min = 3, max = 100)
    private String password;

    // 사용자의 닉네임을 나타내며 최소 3자 이상 50자 이하로 제한
    @NotNull
    @Size(min = 3, max = 50)
    private String nickname;
}