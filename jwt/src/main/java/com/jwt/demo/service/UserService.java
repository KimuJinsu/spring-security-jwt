package com.jwt.demo.service;

import java.util.Collections;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jwt.demo.dto.UserDto;
import com.jwt.demo.entities.Authority;
import com.jwt.demo.entities.User;
import com.jwt.demo.repository.UserRepository;
import com.jwt.demo.util.SecurityUtil;

import lombok.RequiredArgsConstructor;

/**
 * UserService는 사용자와 관련된 로직을 처리하는 서비스 클래스입니다.
 */
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 새로운 사용자를 등록하는 메서드입니다.
     * 이미 존재하는 사용자라면 예외를 발생시킵니다.
     * 
     * @param userDto 등록할 사용자의 정보
     * @return 등록된 User 객체
     */
    @Transactional
    public User signup(UserDto userDto) {
        // 사용자가 이미 존재하는지 확인
        if (userRepository.findOneWithAuthoritiesByUsername(userDto.getUsername()).orElse(null) != null) {
            throw new RuntimeException("이미 가입되어 있는 유저입니다.");
        }

        // 권한 정보 생성
        Authority authority = Authority.builder()
                .authorityName("ROLE_USER")
                .build();

        // 유저 정보 생성 및 저장
        User user = User.builder()
                .username(userDto.getUsername())
                .password(passwordEncoder.encode(userDto.getPassword())) // 비밀번호 암호화
                .nickname(userDto.getNickname())
                .authorities(Collections.singleton(authority))
                .activated(true) // 사용자 활성화 상태
                .build();

        return userRepository.save(user);
    }

    /**
     * 주어진 사용자 이름을 기반으로 사용자와 권한 정보를 조회하는 메서드입니다.
     * 
     * @param username 조회할 사용자의 이름
     * @return 사용자 정보와 권한 정보가 담긴 Optional<User>
     */
    @Transactional(readOnly = true)
    public Optional<User> getUserWithAuthorities(String username) {
        return userRepository.findOneWithAuthoritiesByUsername(username);
    }

    /**
     * 현재 로그인한 사용자 정보를 조회하는 메서드입니다.
     * SecurityUtil을 통해 현재 로그인한 사용자의 이름을 가져와 조회합니다.
     * 
     * @return 현재 사용자 정보와 권한 정보가 담긴 Optional<User>
     */
    @Transactional(readOnly = true)
    public Optional<User> getMyUserWithAuthorities() {
        return SecurityUtil.getCurrentUsername()
                .flatMap(userRepository::findOneWithAuthoritiesByUsername);
    }
}