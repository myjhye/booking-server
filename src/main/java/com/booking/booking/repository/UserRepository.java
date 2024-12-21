package com.booking.booking.repository;

import com.booking.booking.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // 사용자 중복 체크
    boolean existsByEmail(String email);

    // 개별 사용자 삭제
    void deleteByEmail(String email);

    // 개별 사용자 조회
    Optional<User> findByEmail(String email);
}
