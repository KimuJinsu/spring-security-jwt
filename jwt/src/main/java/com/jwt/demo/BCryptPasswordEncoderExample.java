package com.jwt.demo;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class BCryptPasswordEncoderExample {

    public static void main(String[] args) {
        // BCryptPasswordEncoder 객체 생성
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        // 문자열 "12345"를 BCrypt 알고리즘을 통해 암호화
        String result = encoder.encode("12345");
       
        
        // 암호화된 결과 출력
        System.out.println(result);
    }
}