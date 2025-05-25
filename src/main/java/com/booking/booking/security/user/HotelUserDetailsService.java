package com.booking.booking.security.user;

import com.booking.booking.model.User;
import com.booking.booking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

// Spring Security에서 로그인 인증 시, DB에서 사용자 정보를 조회하고
// Spring이 요구하는 UserDetails 형태로 변환해주는 서비스
@Service
@RequiredArgsConstructor
public class HotelUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    // Spring Security가 로그인 시 호출하는 메서드
    // 입력받은 이메일(email)로 사용자 정보를 DB에서 조회하고,
    // 해당 정보를 UserDetails 객체로 변환하여 반환
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        // 1. 이메일을 기준으로 사용자 정보를 DB에서 조회
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("해당 유저를 찾을 수 없습니다"));

        // 2. 조회된 사용자 정보를 Spring Security에서 이해할 수 있는 형태(UserDetails)로 변환
        return HotelUserDetails.buildUserDetails(user);

        /*
            반환되는 UserDetails에는 다음 정보가 포함됨:
            - 사용자 이메일 (username)
            - 암호화된 비밀번호
            - 권한 목록 (예: ROLE_USER, ROLE_ADMIN)
         */
    }
}
