package com.booking.booking.repository;

import com.booking.booking.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    // 역할 이름으로 역할 조회
    Optional<Role> findByName(String role);

    // 역할 이름으로 중복 체크 --> 역할은 각자 하나만 존재해야 함 (중복 존재 X)
    boolean existsByName(String role);
}
