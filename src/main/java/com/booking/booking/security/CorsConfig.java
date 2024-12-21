package com.booking.booking.security;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.Arrays;


// 다른 출처에서 오는 요청을 어디까지 허용할지 정하는 설정 파일
@Configuration
@EnableWebMvc
public class CorsConfig {

    // CORS 설정 유효 시간 3600초(1시간)으로 설정 --> 이 요청이 안전한지 물어보는 주기가 1시간인 것을 의미
    private static final Long MAX_AGE = 3600L;
    // CORS 필터의 우선 순위를 설정 (-102는 높은 우선순위를 의미) --> 다른 필터들보다 먼저 실행되어야 CORS 정책을 올바르게 실행 가능
    private static final int CORS_FILTER_ORDER = -102;

    @Bean
    public FilterRegistrationBean corsFilter() {

        // CORS 설정을 "URL 패턴별"로 적용할 수 있게 해주는 클래스 --> 여기서는 "/**" 로 사용
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // CORS 정책을 구성하는 클래스의 인스턴스 생성
        CorsConfiguration config = new CorsConfiguration();

        // credentials 설정 --> true 설정 시 쿠키 및 인증 헤더를 포함한 요청 허용
        config.setAllowCredentials(true);

        // 1. 이 주소에서 오는 요청만 허용해라
        config.addAllowedOrigin("http://localhost:5173");

        // 2. 이런 헤더들을 허용해라
        config.setAllowedHeaders(Arrays.asList(
                HttpHeaders.AUTHORIZATION,
                HttpHeaders.CONTENT_TYPE,
                HttpHeaders.ACCEPT));

        // 3. 이런 요청 방식들을 허용해라
        config.setAllowedMethods(Arrays.asList(
                HttpMethod.GET.name(),
                HttpMethod.POST.name(),
                HttpMethod.PUT.name(),
                HttpMethod.DELETE.name()));

        // preflight: 요청의 캐시 시간 설정 --> 실제 요청 전에 예비 요청을 보내 안전한지 확인
        config.setMaxAge(MAX_AGE);

        // 모든 경로(/**)에 대해 위에서 설정한 CORS 정책 적용
        source.registerCorsConfiguration("/**", config);

        // CORS 필터 등록 --> 필터 등록 클래스(FilterRegistrationBean) 사용
        FilterRegistrationBean bean = new FilterRegistrationBean(new CorsFilter(source));

        // 필터의 실행 순서 설정
        bean.setOrder(CORS_FILTER_ORDER);

        return bean;
    }
}
