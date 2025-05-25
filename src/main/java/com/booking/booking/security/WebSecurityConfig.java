package com.booking.booking.security;

import com.booking.booking.security.jwt.AuthTokenFilter;
import com.booking.booking.security.jwt.JwtAuthEntryPoint;
import com.booking.booking.security.user.HotelUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


// Spring Security의 핵심 보안 설정 클래스
@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true, prePostEnabled = true)
public class WebSecurityConfig {

    // 사용자 인증 시 DB에서 사용자 정보를 조회하는 서비스
    private final HotelUserDetailsService userDetailsService;

    // 인증 실패(401 Unauthorized) 시 처리할 핸들러 (ex: 토큰이 없거나 유효하지 않을 때)
    private final JwtAuthEntryPoint jwtAuthEntryPoint;

    // JWT 토큰을 파싱하고 인증 정보를 추출하는 필터
    @Bean
    public AuthTokenFilter authenticationTokenFilter() {
        return new AuthTokenFilter();
    }

    // 비밀번호 암호화 방식 (BCrypt 사용)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 인증 처리 제공자: 사용자 정보와 비밀번호를 기반으로 인증 수행
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        var authProvider = new DaoAuthenticationProvider();

        // 사용자 정보는 우리가 정의한 서비스로 조회
        authProvider.setUserDetailsService(userDetailsService);
        // 비밀번호는 Bcrypt 방식으로 검증
        authProvider.setPasswordEncoder(passwordEncoder());

        // 검증 성공 시 인증된 사용자 정보 생성
        return authProvider;
    }


    // 인증 매니저: authenticate() 호출 시 실제 인증을 담당하는 핵심 컴포넌트
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    // 전체 보안 정책 설정: 어떤 URL에 어떤 권한이 필요한지, 필터 적용 순서 등
    @Bean
    public SecurityFilterChain filterChain (HttpSecurity http) throws Exception {

        http
            // JWT를 사용하므로 CSRF 비활성화
            .csrf(AbstractHttpConfigurer :: disable)
            // 인증 실패 시 처리할 핸들러 지정
            .exceptionHandling((exception) -> exception.authenticationEntryPoint(jwtAuthEntryPoint))
            // 세션을 사용하지 않음 (JWT는 Stateless이기 때문)
            .sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            // URL 별 접근 권한 설정
            .authorizeHttpRequests(
                    (auth) -> auth
                        // 누구나 접근 가능
                        .requestMatchers("/auth/**", "/rooms/**", "/boards/**").permitAll()
                        // 관리자만 접근 가능
                        .requestMatchers("/roles/**").hasRole("ADMIN")
                        // 로그인한 사용자만 접근 가능
                        .anyRequest().authenticated()
            );

        // 사용자 인증 방식 설정 (DaoAuthenticationProvider 사용)
        http.authenticationProvider(authenticationProvider());

        // UsernamePasswordAuthenticationFilter 전에 JWT 필터를 먼저 실행
        http.addFilterBefore(
                authenticationTokenFilter(),
                UsernamePasswordAuthenticationFilter.class
        );

        // 위 같이 설정한 대로 최종적으로 보안 필터 체인 생성
        return http.build();
    }


}
