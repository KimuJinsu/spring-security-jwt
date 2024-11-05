package com.jwt.demo.util;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * SecurityUtil은 현재 인증된 사용자의 이름을 가져오는 유틸리티 클래스입니다.
 * SecurityContextHolder에서 인증 정보를 추출하여 사용자의 이름을 반환합니다.
 */
public class SecurityUtil {

    private static final Logger logger = LoggerFactory.getLogger(SecurityUtil.class);

    // 인스턴스 생성 방지를 위한 private 생성자
    // 객체가 불필요하게 생성되는 것을 막기 위해 클래스의 생성자를 private으로 설정하는 것입니다. 
    // 이 기법은 주로 유틸리티 클래스나 싱글톤 패턴을 구현할 때 사용됩니다.
    private SecurityUtil() {}

    /**
     * 현재 인증된 사용자의 이름을 Optional로 반환합니다.
     * 
     * @return 현재 사용자 이름이 담긴 Optional<String> 객체
     */
    public static Optional<String> getCurrentUsername() {

        // SecurityContextHolder에서 인증 객체 가져오기
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 인증 객체가 없는 경우 빈 Optional 반환
        if (authentication == null) {
            logger.debug("Security Context에 인증 정보가 없습니다.");
            return Optional.empty();
        }

        // 사용자 이름을 담을 변수
        String username = null;
        
        // 인증 객체의 Principal 타입에 따라 사용자 이름 가져오기
        if (authentication.getPrincipal() instanceof UserDetails springSecurityUser) {
            // UserDetails 타입인 경우 getUsername()을 통해 이름 추출
            username = springSecurityUser.getUsername();
        } else if (authentication.getPrincipal() instanceof String) {
            // Principal이 문자열인 경우 직접 사용자 이름으로 캐스팅
            username = (String) authentication.getPrincipal();
        }

        // Optional로 반환하여 null 처리 가능하도록 함
        return Optional.ofNullable(username);
    }
}