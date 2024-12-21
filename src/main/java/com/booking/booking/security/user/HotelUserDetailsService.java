package com.booking.booking.security.user;

import com.booking.booking.model.User;
import com.booking.booking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

// Spring Security 인증을 위해 DB의 User 정보를 찾고 UserDetails 타입으로 변환하는 서비스
@Service
@RequiredArgsConstructor
public class HotelUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    // 이메일로 사용자를 찾아서 Spring Security가 이해할 수 있는 형태로 변환해주자
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        // 1. 이메일로 DB에서 사용자를 찾아보자
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("해당 유저를 찾을 수 없습니다"));

        // 2. 찾은 사용자 정보를 Spring Security가 쓸 수 있게 변환해서 돌려주자
        // DB에서 찾아온 엔티티를 Security가 이해할 수 있는 형태(UserDetails)로 변환
        return HotelUserDetails.buildUserDetails(user);

        /*
            변환되는 정보
            - 기본 정보: 이메일, 비밀번호
            - 부가 정보: 권한 목록 (ROLE_USER, ROLE_ADMIN 등)
         */
    }
}
