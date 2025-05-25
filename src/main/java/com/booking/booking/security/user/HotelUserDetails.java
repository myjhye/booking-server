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


// DB에서 가져온 사용자 정보를 Spring Security에서 사용할 수 있도록 변환하는 클래스
// Spring Security는 이 UserDetails 객체를 통해 인증된 사용자 정보를 추적함
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class HotelUserDetails implements UserDetails {

    private Long id; // 사용자 고유 ID
    private String email; // 사용자 이메일 (Spring Security에서 username으로 사용됨)
    private String password; // 암호화된 비밀번호
    private Collection<GrantedAuthority> authorities; // 사용자의 권한 목록 (ROLE_USER, ROLE_ADMIN 등)
    private User user; // 원본 User 엔티티 (필요 시 서비스 레벨에서 접근 가능)

    // --- 정적 메서드: DB User 객체 → Spring Security용 UserDetails 객체로 변환 ---
    public static HotelUserDetails buildUserDetails(User user) {

        // 사용자 Role 리스트를 Spring Security 권한 객체로 변환
        List<GrantedAuthority> authorities = user.getRoles()
                                                 .stream()
                                                 .map((role) -> new SimpleGrantedAuthority(role.getName()))
                                                 .collect(Collectors.toList());

        // 변환된 정보를 담은 HotelUserDetails 객체 생성
        return new HotelUserDetails(
                user.getId(),
                user.getEmail(),
                user.getPassword(),
                authorities,
                user
        );
    }

    // --- UserDetails 인터페이스 구현 메서드들 ---

    // Spring Security가 사용자 권한 조회 시 호출
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    // Spring Security가 비밀번호 비교 시 호출
    @Override
    public String getPassword() {
        return password;
    }

    // Spring Security가 사용자 식별 시 호출 (username 역할, 여기선 email)
    @Override
    public String getUsername() {
        return email;
    }


    // --- 아래 메서드들은 계정 상태 관련 여부를 판단 (예: 계정 잠금 등) ---

    // 계정이 만료되지 않았는지 여부
    @Override
    public boolean isAccountNonExpired() {
        return true; // 항상 유효한 계정으로 간주
    }

    // 계정이 잠겨있지 않은지 여부
    @Override
    public boolean isAccountNonLocked() {
        return true; // 항상 잠금 상태 아님
    }

    // 비밀번호가 만료되지 않았는지 여부
    @Override
    public boolean isCredentialsNonExpired() {
        return true; // 항상 유효한 비밀번호로 간주
    }

    // 계정이 사용 가능한 상태인지 여부
    @Override
    public boolean isEnabled() {
        return true; // 항상 활성화된 계정으로 간주
    }
}
