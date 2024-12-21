package com.booking.booking.security.jwt;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.http.MediaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


// 인증 실패 시 에러 처리 핸들러
@Component
public class JwtAuthEntryPoint implements AuthenticationEntryPoint {

    // 에러 로그를 남길 도구
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthEntryPoint.class);

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {

        // 응답 JSON 형식으로 설정
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        // 401 Unauthorized 상태 코드 설정
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        // 에러 응답 데이터 만들기
        final Map<String, Object> body = new HashMap<>();
        body.put("status", HttpServletResponse.SC_UNAUTHORIZED); // 상태 코드: 401
        body.put("error", "Unauthorized"); // 에러 종류: 인증 실패
        body.put("message", authException.getMessage()); // 에러 메시지
        body.put("path", request.getServletPath()); // 요청 경로

        // 만든 에러 응답을 JSON으로 변환해서 클라이언트에게 보내기
        final ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getOutputStream(), body);

        /*
            클라이언트가 받는 JSON 응답:
            {
               "status": 401,
               "error": "Unauthorized",
               "message": "인증에 실패했습니다",
               "path": "/api/some-protected-route"
            }
         */

    }
}
