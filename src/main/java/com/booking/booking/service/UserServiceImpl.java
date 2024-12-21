package com.booking.booking.service;

import com.booking.booking.exception.UserAlreadyExistsException;
import com.booking.booking.model.Role;
import com.booking.booking.model.User;
import com.booking.booking.repository.RoleRepository;
import com.booking.booking.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;


    // 회원가입
    @Override
    public User registerUser(User user) {

        // 1. 이메일 중복 체크
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new UserAlreadyExistsException("이미 등록된 이메일입니다: " + user.getEmail());
        }

        // 2. 비밀번호 암호화
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // 3. 기본 권한 설정 (일반 사용자 권한 "ROLE_USER" 부여)
        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("기본 권한이 없습니다"));
        user.setRoles(Collections.singletonList(userRole));

        // 4. DB에 사용자 정보 저장
        return userRepository.save(user);
    }

    @Override
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @Override
    public void deleteUser(String email) {
        getUser(email);
        userRepository.deleteByEmail(email);
    }

    @Override
    public User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("해당 유저를 찾을 수 없습니다"));
    }
}
