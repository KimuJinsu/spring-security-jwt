package com.jwt.demo.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.GrantedAuthority;

import com.jwt.demo.entities.User;
import com.jwt.demo.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * CustomUserDetailsService는 사용자 인증 정보를 제공하는 서비스 클래스입니다.
 */
@Slf4j
@Service("userDetailsService")
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 주어진 사용자 이름(username)을 기반으로 UserDetails 객체를 반환합니다.
     * 
     * @param username 사용자 이름
     * @return UserDetails 사용자 인증 정보 객체
     * @throws UsernameNotFoundException 사용자 정보를 찾을 수 없을 때 발생하는 예외
     */
    @Override
    @Transactional
    public UserDetails loadUserByUsername(final String username) {
        log.info("+loadUserByname");
        
        // 사용자 이름으로 사용자 정보를 조회하고, 없을 시 예외를 발생시킵니다.
        UserDetails userDetails = userRepository.findOneWithAuthoritiesByUsername(username)
                .map(user -> createUser(username, user))
                .orElseThrow(() -> new UsernameNotFoundException(username + " -> 데이터베이스에서 찾을 수 없습니다."));
        
        log.info("-loadUserByname");
        return userDetails;
    }

    /**
     * User 객체를 UserDetails 객체로 변환하는 메서드입니다.
     * 
     * @param username 사용자 이름
     * @param user 사용자 엔티티 객체
     * @return UserDetails 변환된 사용자 인증 정보 객체
     */
    private org.springframework.security.core.userdetails.User createUser(String username, User user) {
        // 사용자가 활성화되어 있지 않으면 예외를 발생시킵니다.
        if (!user.isActivated()) {
            throw new RuntimeException(username + " -> 활성화되어 있지 않습니다.");
        }
        
        log.info("createUser: username=" + username);

        // 사용자의 권한을 GrantedAuthority 형태로 변환합니다.
        List<GrantedAuthority> grantedAuthorities = user.getAuthorities().stream()
                .map(authority -> new SimpleGrantedAuthority(authority.getAuthorityName()))
                .collect(Collectors.toList());

        // UserDetails 객체를 생성합니다.
        org.springframework.security.core.userdetails.User uds = 
        		(org.springframework.security.core.userdetails.User) org.springframework.security.core.userdetails.
        		User.withUsername(username)
        		    .password(user.getPassword())
        		    .authorities(grantedAuthorities)
        		    .build();
        log.info("-createUser");
        return uds;
    }
}