package com.jwt.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.jwt.demo.entities.RefreshToken;

/**
 * RefreshTokenRepository는 JPA를 통해 RefreshToken 엔티티에 대한 CRUD 작업을 제공하는 인터페이스입니다.
 * JpaRepository 인터페이스를 상속받아 기본적인 데이터베이스 연산 메서드를 사용할 수 있습니다.
 * RefreshToken 엔티티는 'token' 필드를 기본 키로 사용하기 때문에, 기본 키 타입을 String으로 지정합니다.
 */
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {

	 // RefreshToken 엔티티의 token 필드를 기준으로 RefreshToken 엔티티를 찾는 메서드 선언
    Optional<RefreshToken> findByToken(String token);
}