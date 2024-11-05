package com.jwt.demo.jwt;

import java.security.Key;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import com.jwt.demo.entities.RefreshToken;
import com.jwt.demo.repository.RefreshTokenRepository;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

//import com.jwt.demo.User;
import org.springframework.security.core.userdetails.User;

@Slf4j // 로깅 기능을 위한 애노테이션
@Component // Spring Bean으로 등록되어 애플리케이션 전역에서 사용 가능하도록 함
public class TokenProvider implements InitializingBean {

    // JWT 토큰에 권한 정보를 저장할 키의 상수값입니다.
    private static final String AUTHORITIES_KEY = "auth";

    // JWT 토큰 서명을 위한 비밀 키 문자열로, application.properties에서 주입받습니다.
    private final String secret;

    // AccessToken의 유효시간 (밀리초)으로, application.properties에서 주입받습니다.
    private final long accessTokenValidityInMilliseconds;

    // RefreshToken의 유효시간 (밀리초)으로, application.properties에서 주입받습니다.
    private final long refreshTokenValidityInMilliseconds;

    // JWT 서명 및 검증에 사용할 Key 객체입니다.
    private Key key;

    // RefreshToken 정보를 저장하기 위한 JPA Repository로, 의존성 주입됩니다.
    private final RefreshTokenRepository refreshTokenRepository;

    /**
     * 생성자: JWT 관련 설정값들을 주입받아 초기화합니다.
     *
     * @param secret JWT 서명에 사용할 비밀 키 문자열. 이 비밀 키는 서버에서 생성한 JWT의 진위를 검증하는 데 사용됩니다.
     * @param accessTokenValidityInSeconds AccessToken의 유효기간 (초 단위).
     * @param refreshTokenValidityInSeconds RefreshToken의 유효기간 (초 단위).
     * @param refreshTokenRepository RefreshToken을 저장하는 JPA Repository.
     */
    public TokenProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.token-validity-in-seconds}") long accessTokenValidityInSeconds,
            @Value("${jwt.refreshtoken-validity-in-seconds}") long refreshTokenValidityInSeconds,
            RefreshTokenRepository refreshTokenRepository) {
        this.secret = secret; // 비밀 키 할당
        this.accessTokenValidityInMilliseconds = accessTokenValidityInSeconds * 1000; // AccessToken 유효시간을 밀리초로 변환하여 할당
        this.refreshTokenValidityInMilliseconds = refreshTokenValidityInSeconds * 1000; // RefreshToken 유효시간을 밀리초로 변환하여 할당
        this.refreshTokenRepository = refreshTokenRepository; // Repository 할당
    }

    /**
     * 초기화 후 Base64로 인코딩된 secret 키를 디코딩하여 key 변수에 저장합니다.
     */
    @Override
    public void afterPropertiesSet() {
        // Secret 키를 디코딩하여 JWT 서명에 사용할 키 객체를 생성합니다.
        byte[] keyBytes = Decoders.BASE64.decode(secret); 
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * JWT 토큰을 생성합니다.
     * @param authentication 인증 정보를 포함하는 Authentication 객체
     * @param isAccessToken true일 경우 AccessToken을 생성, false일 경우 RefreshToken을 생성
     * @return 생성된 JWT 토큰 (JSON Web Token)
     */
    public String createToken(Authentication authentication, boolean isAccessToken) {
        // 사용자의 권한 정보를 문자열로 변환. 예: "ROLE_USER,ROLE_ADMIN"
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        // 현재 시간과 토큰의 만료 시간을 설정합니다.
        long now = (new Date()).getTime();
        long expiryDate = isAccessToken ? now + accessTokenValidityInMilliseconds : now + refreshTokenValidityInMilliseconds;
        Date validity = new Date(expiryDate); // 만료 시간 설정

        // JWT 빌더를 통해 토큰을 생성합니다.
        return Jwts.builder()
                .setSubject(authentication.getName()) // 사용자 정보 설정 (토큰의 subject)
                .claim(AUTHORITIES_KEY, authorities) // 권한 정보를 클레임으로 저장
                .signWith(key, SignatureAlgorithm.HS512) // 서명 알고리즘과 키 설정
                .setExpiration(validity) // 만료 시간 설정
                .compact(); // 최종적으로 토큰을 생성하여 반환
    }

    /**
     * RefreshToken을 생성하고 데이터베이스에 저장합니다.
     * @param authentication 인증 정보를 포함하는 Authentication 객체
     * @return 생성된 RefreshToken 문자열
     */
    public String createAndPersistRefreshTokenForUser(Authentication authentication) {
        String refreshToken = this.createToken(authentication, false); // RefreshToken 생성

        // 만료 날짜 설정
        long now = (new Date()).getTime();
        Date validity = new Date(now + refreshTokenValidityInMilliseconds);
        Instant instant = validity.toInstant();
        LocalDateTime expiryDate = instant.atZone(ZoneId.systemDefault()).toLocalDateTime();

        // RefreshToken 엔티티 생성 및 저장
        String username = authentication.getName();
        RefreshToken refreshTokenEntity = RefreshToken.builder()
                .username(username)
                .token(refreshToken)
                .expiryDate(expiryDate)
                .build();
        refreshTokenRepository.save(refreshTokenEntity);

        return refreshToken;
    }

    /**
     * 토큰을 사용해 인증 정보를 반환합니다.
     * @param token JWT 토큰
     * @return Authentication 객체
     */
    public Authentication getAuthentication(String token) {
        // 서명 키를 사용하여 JWT를 파싱하고 클레임을 추출합니다.
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        // 권한 정보 추출
        Collection<? extends GrantedAuthority> authorities = Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        // 인증 객체 생성
        User principal = new User(claims.getSubject(), "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    /**
     * JWT 토큰의 유효성을 검증합니다.
     * @param token 검증할 토큰
     * @return 유효한 토큰이면 true, 그렇지 않으면 false
     */
    public boolean validateToken(String token) {
        try {
            // 서명 키를 사용하여 JWT를 파싱하여 유효성을 검증합니다.
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            log.info("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.info("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.info("JWT 토큰이 잘못되었습니다.");
        }
        return false; // 토큰이 유효하지 않은 경우
    }
}