package com.jwt.demo.entities;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore; // JSON 직렬화 시 해당 필드를 무시하는 애노테이션

import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * User 엔티티는 사용자 정보를 저장하는 클래스입니다.
 * 사용자 이름, 비밀번호, 닉네임, 활성화 상태, 권한 목록을 포함하고 있습니다.
 * 이는 데이터베이스의 users 테이블과 매핑됩니다.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users") // 이 클래스가 데이터베이스의 users 테이블에 매핑됨을 나타냅니다.
public class User {

    /**
     * 사용자 ID를 저장하는 필드로, 기본 키 역할을 합니다.
     * 데이터베이스에서 자동 생성됩니다.
     */
    @JsonIgnore // JSON 응답에서 userId가 노출되지 않도록 설정
    @Id // 기본 키 필드임을 나타냅니다.
    @Column(name = "user_id") // 데이터베이스 테이블의 user_id 컬럼과 매핑됩니다.
    @GeneratedValue(strategy = GenerationType.IDENTITY) // ID가 데이터베이스에서 자동 생성되도록 설정
    private Long userId;

    /**
     * 사용자 이름을 저장하는 필드입니다. 고유 값으로 설정됩니다.
     * 사용자 이름은 최대 50자까지 허용됩니다.
     */
    @Column(name = "username", length = 50, unique = true) // 중복을 허용하지 않도록 unique 설정
    private String username;

    /**
     * 비밀번호를 저장하는 필드입니다. JSON 직렬화 시 노출되지 않습니다.
     */
    @JsonIgnore // JSON 응답에 비밀번호를 노출하지 않도록 설정
    @Column(name = "password", length = 100) // 비밀번호의 최대 길이는 100자입니다.
    private String password;

    /**
     * 닉네임을 저장하는 필드입니다. 최대 50자까지 허용됩니다.
     */
    @Column(name = "nickname", length = 50) // 데이터베이스의 nickname 컬럼과 매핑
    private String nickname;

    /**
     * 사용자의 활성화 상태를 나타내는 필드입니다.
     * true이면 활성화된 상태, false이면 비활성화된 상태를 의미합니다.
     * JSON 직렬화 시에는 노출되지 않습니다.
     */
    @JsonIgnore // JSON 응답에서 활성화 상태를 노출하지 않음
    @Column(name = "activated") // 데이터베이스의 activated 컬럼과 매핑
    private boolean activated;

    /**
     * 사용자와 연결된 권한(Authority) 목록을 나타내는 필드입니다.
     * 다대다 관계를 설정하여 사용자의 권한을 관리합니다.
     */
    @ManyToMany // User 엔티티와 Authority 엔티티 간 다대다 관계를 설정
    @JoinTable(
            name = "user_authority", // 연결 테이블 이름
            joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "user_id")}, // user 테이블의 ID와 연결
            inverseJoinColumns = {@JoinColumn(name = "authority_name", referencedColumnName = "authority_name")}) // authority 테이블의 이름과 연결
    private Set<Authority> authorities; // 사용자에게 할당된 권한 목록을 저장
}

	/*Authority 엔티티에는 ManyToMany 연관관계의 반대쪽이 설정되지 않은 이유는 설계 의도에 따라 
	 * 양방향 연관관계가 필요하지 않기 때문입니다. 이 경우 Authority는 User가 어떤 권한을 
	 * 가지고 있는지에 대한 정보만을 제공하고, 권한이 User에 대한 참조를 가지지 않아도 되도록 설계되었습니다.
	   이렇게 단방향으로만 연관관계를 설정하면 코드가 더 단순해지고, 양방향 참조가 필요 없는 경우 성능 최적화에도 도움이 됩니다. 
	   User 엔티티에서는 권한 정보에 접근할 수 있지만, 
	   Authority에서는 User 정보를 알 필요가 없기 때문에 @ManyToMany가 User 쪽에만 설정된 것입니다.
	   즉, 필요한 방향에서만 참조하도록 설계된 것입니다.*/