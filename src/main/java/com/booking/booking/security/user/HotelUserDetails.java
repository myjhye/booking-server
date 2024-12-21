package com.booking.booking.security.user;

import com.booking.booking.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;


// DB에서 가져온 사용자 정보를 Spring Security가 이해할 수 있는 형태로 변환
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class HotelUserDetails implements UserDetails {
    // 사용자 정보
    private Long id;
    private String email;
    private String password;
    private Collection<GrantedAuthority> authorities;

    // DB의 User 정보를 Spring Security용 UserDetails로 변환해주자
    public static HotelUserDetails buildUserDetails(User user) {

        // 사용자의 권한 정보를 Spring Security가 이해할 수 있는 형태로 변환
        List<GrantedAuthority> authorities = user.getRoles()
                                                 .stream()
                                                 .map((role) -> new SimpleGrantedAuthority(role.getName()))
                                                 .collect(Collectors.toList());

        // 변환된 정보로 UserDetails 객체 생성
        return new HotelUserDetails(
                user.getId(),
                user.getEmail(),
                user.getPassword(),
                authorities
        );
    }

    // --- Spring Security가 필요로 하는 기본 정보들 ---

    // 이 사용자의 권한 목록
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    // 비밀번호
    @Override
    public String getPassword() {
        return password;
    }

    // 사용자 식별용 이메일
    @Override
    public String getUsername() {
        return email;
    }


    // --- 계정 상태 검사 메소드들 (여기서는 모두 true로 설정) ---

    // 계정 만료 없음
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    // 계정 잠금 없음
    @Override
    public boolean isAccountNonLocked() {
        // 항상 true 반환 = 계정 잠금 없음
        return true;
    }

    // 비밀번호 만료 없음
    @Override
    public boolean isCredentialsNonExpired() {
        // 항상 true 반환 = 비밀번호 만료 없음
        return true;
    }

    // 계정이 항상 활성화
    @Override
    public boolean isEnabled() {
        // 항상 true 반환 = 계정 항상 활성화
        return true;
    }
}
