package com.jwt.demo.entities;

import java.time.LocalDateTime; // 토큰 만료 시간을 저장하기 위해 사용되는 LocalDateTime 클래스입니다.

import com.jwt.demo.dto.TokenDto; // 토큰 데이터 전송 객체 (DTO)와 연결할 때 사용됩니다.

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * RefreshToken 엔티티는 JWT 리프레시 토큰 정보를 저장하는 클래스입니다.
 * 리프레시 토큰과 관련된 사용자 이름, 만료 시간을 포함합니다.
 * 이 클래스는 데이터베이스에 리프레시 토큰을 저장하는 데 사용됩니다.
 */
@Getter  // 모든 필드에 대해 getter 메서드를 자동 생성해주는 Lombok 애노테이션입니다.
@Setter  // 모든 필드에 대해 setter 메서드를 자동 생성해주는 Lombok 애노테이션입니다.
@NoArgsConstructor // 기본 생성자를 자동 생성합니다.
@AllArgsConstructor // 모든 필드를 매개변수로 받는 생성자를 자동 생성합니다.
@Builder            // 빌더 패턴을 제공하여 RefreshToken 객체 생성 시 가독성을 높여줍니다.
@Entity             // 이 클래스가 JPA 엔티티임을 나타내는 애노테이션입니다.
public class RefreshToken {

    /**
     * 리프레시 토큰 문자열을 저장하는 필드로, 기본 키 역할을 합니다.
     * 토큰 자체가 고유한 값으로 기본 키로 설정되었습니다.
     */
    @Id // 이 필드를 엔티티의 기본 키로 지정하여 데이터베이스 내에서 고유성을 보장합니다.
    private String token;

    /**
     * 리프레시 토큰과 연결된 사용자 이름을 저장하는 필드입니다.
     */
    private String username;

    /**
     * 리프레시 토큰의 만료 시간을 저장하는 필드입니다.
     * LocalDateTime 타입으로 저장하여 날짜와 시간을 함께 관리할 수 있습니다.
     */
    private LocalDateTime expiryDate;
}