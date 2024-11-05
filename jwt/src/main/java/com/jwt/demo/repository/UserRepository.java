package com.jwt.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.jwt.demo.entities.User;

/**
 * UserRepository는 User 엔티티에 대한 데이터베이스 상호작용을 위한 인터페이스입니다.
 * JpaRepository<User, Long>을 상속하여 기본적인 CRUD 작업 메서드를 자동으로 제공합니다.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * ID를 기반으로 User를 조회하는 메서드입니다.
     * 
     * @param id 조회할 사용자의 ID
     * @return Optional로 감싸진 User 객체
     */
    Optional<User> findById(Long id);

    /**
     * User 엔티티를 저장하는 메서드입니다.
     * 
     * @param user 저장할 User 객체
     * @return 저장된 User 객체
     */
    User save(User user);

    /**
     * 사용자 이름을 기반으로, 권한 정보와 함께 User 객체를 조회하는 메서드입니다.
     * 
     * @param username 조회할 사용자의 이름
     * @return Optional로 감싸진 User 객체, 권한 정보 포함
     */
    Optional<User> findOneWithAuthoritiesByUsername(String username);

    /**
     * User 엔티티를 삭제하는 메서드입니다.
     * 
     * @param user 삭제할 User 객체
     */
    void delete(User user);
}