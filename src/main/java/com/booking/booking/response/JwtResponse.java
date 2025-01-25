package com.booking.booking.response;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

// 클라이언트 로그인 성공 후 응답 데이터 (DTO)
@Data
@NoArgsConstructor
public class JwtResponse {

    private Long id;
    private String email;
    private String accessToken;
    private String refreshToken;
    private String type = "Bearer";
    private List<String> roles;

    public JwtResponse(Long id, String email, String accessToken, String refreshToken, List<String> roles) {
        this.id = id;
        this.email = email;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.roles = roles;
    }
}
