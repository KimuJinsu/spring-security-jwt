package com.jwt.demo;

import java.util.Collections;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.jwt.demo.dto.UserDto;
import com.jwt.demo.entities.Authority;
import com.jwt.demo.entities.User;
import com.jwt.demo.repository.UserRepository;

@SpringBootApplication
public class DemoJwtApplication {
    
    // CommandLineRunner 빈 정의
    @Bean
    public CommandLineRunner dataLoader(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
            ) {
        
        return new CommandLineRunner() {
              @Override
              public void run(String... args) throws Exception {
                  
                  // 1. Authority 엔티티 생성: ROLE_USER 권한을 가진 Authority 객체를 생성합니다.
                  Authority authority = Authority.builder()
                          .authorityName("ROLE_USER")
                          .build();
                  
                  // 2. UserDto 생성: 사용자 정보를 가진 UserDto 객체를 생성합니다.
                  UserDto userDto = UserDto.builder()
                          .username("intheeast0305@gmail.com")
                          .password("12345")
                          .nickname("sungwon")
                          .build();
                  
                  // 3. User 엔티티 생성:
                  // UserDto로부터 username, password, nickname 정보를 받아 User 객체를 생성합니다.
                  // password는 PasswordEncoder를 사용하여 해싱 처리합니다.
                  User user = User.builder()
                          .username(userDto.getUsername())
                          .password(passwordEncoder.encode(userDto.getPassword()))
                          .nickname(userDto.getNickname())
                          .authorities(Collections.singleton(authority)) // Set으로 Authority 추가
                          .activated(true) // 계정 활성화 여부 설정
                          .build();

                  // 4. UserRepository를 통해 DB에 저장
                  userRepository.save(user);
                  
                  //  ROLE_ADMIN 권한 생성
                  Authority adminAuthority = Authority.builder()
                          .authorityName("ROLE_ADMIN")
                          .build();
                  
                  //  admin 계정 생성
                  UserDto adminDto = UserDto.builder()
                          .username("admin@example.com")
                          .password("12345") // admin 비밀번호 설정
                          .nickname("admin")
                          .build();
                  
                  User admin = User.builder()
                          .username(adminDto.getUsername())
                          .password(passwordEncoder.encode(adminDto.getPassword()))
                          .nickname(adminDto.getNickname())
                          .authorities(Collections.singleton(adminAuthority))
                          .activated(true)
                          .build();

                  userRepository.save(admin);
              }
              
              
        };
        
    }

    // main 메서드: Spring Boot 애플리케이션을 시작
    public static void main(String[] args) {
        SpringApplication.run(DemoJwtApplication.class, args);
    }

}