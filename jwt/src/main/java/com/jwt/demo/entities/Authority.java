package com.jwt.demo.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Authority 엔티티는 사용자의 권한 정보를 담고 있는 클래스입니다.
 * 주로 ROLE_USER, ROLE_ADMIN과 같은 역할을 관리하는 데 사용됩니다.
 */
@Getter  // 모든 필드에 대해 getter 메서드를 자동으로 생성해주는 Lombok 애노테이션입니다.
@Setter  // 모든 필드에 대해 setter 메서드를 자동으로 생성해주는 Lombok 애노테이션입니다.
@Builder // 빌더 패턴을 제공하여 Authority 객체 생성 시 가독성을 높여줍니다.
@AllArgsConstructor // 모든 필드를 매개변수로 받는 생성자를 자동으로 생성합니다.
@NoArgsConstructor  // 기본 생성자를 자동으로 생성합니다.
@Entity             // 이 클래스가 JPA 엔티티임을 나타내는 애노테이션입니다.
@Table(name = "authority") // 데이터베이스의 authority 테이블과 매핑되도록 지정합니다.
public class Authority {

    /**
     * 권한 이름을 나타내는 필드로, 기본 키 역할을 합니다.
     * 예를 들어 ROLE_USER, ROLE_ADMIN과 같은 권한 이름을 저장합니다.
     */
    @Id // 이 필드를 엔티티의 기본 키로 지정합니다.
    @Column(name = "authority_name", length = 50) 
    // authority_name이라는 컬럼에 매핑되며, 최대 길이는 50자로 제한됩니다.
    private String authorityName;
}