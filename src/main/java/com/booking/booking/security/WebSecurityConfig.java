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


// Spring Security의 핵심 설정
@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true, prePostEnabled = true)
public class WebSecurityConfig {

    // DB에서 사용자 정보 가져와라
    private final HotelUserDetailsService userDetailsService;

    // 인증 실패하면 이걸로 처리해라
    private final JwtAuthEntryPoint jwtAuthEntryPoint;

    // JWT 토큰은 이걸로 검증해라
    @Bean
    public AuthTokenFilter authenticationTokenFilter() {
        return new AuthTokenFilter();
    }

    // 비밀번호는 이걸로 암호화해라
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 사용자 인증은 이걸로 처리해라
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        var authProvider = new DaoAuthenticationProvider();

        // 사용자 정보는 이 서비스로 찾고
        authProvider.setUserDetailsService(userDetailsService);
        // 비밀번호는 이걸로 검증해라
        authProvider.setPasswordEncoder(passwordEncoder());

        // 검증 성공 시 인증된 사용자 정보 생성
        return authProvider;
    }


    // 인증 관리는 이 매니저가 총괄한다
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    // 보안 규칙은 이렇게 설정한다
    @Bean
    public SecurityFilterChain filterChain (HttpSecurity http) throws Exception {

        http
            // CSRF 공격은 JWT 쓸 거니까 막지 마라
            .csrf(AbstractHttpConfigurer :: disable)
            // 인증 실패하면 이 핸들러로 처리해라
            .exceptionHandling((exception) -> exception.authenticationEntryPoint(jwtAuthEntryPoint))
            // 세션은 JWT 쓸 거니까 사용하지 마라
            .sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            // URL별 접근 권한은 이렇게 설정한다
            .authorizeHttpRequests((auth) -> auth
                                                // 누구나 접근 가능
                                                .requestMatchers("/auth/**", "/rooms/**")
                                                // 관리자만 접근 가능
                                                .permitAll().requestMatchers("/roles/**").hasRole("ADMIN")
                                                // 로그인한 사용자만 접근 가능
                                                .anyRequest().authenticated());

        // 인증은 위 방식으로 처리해라
        http.authenticationProvider(authenticationProvider());

        // 모든 요청은 JWT 토큰 검증을 먼저해라
        http.addFilterBefore(
                authenticationTokenFilter(),
                UsernamePasswordAuthenticationFilter.class
        );

        // 위 같이 설정한 대로 보안 필터 체인 생성해라
        return http.build();
    }


}
