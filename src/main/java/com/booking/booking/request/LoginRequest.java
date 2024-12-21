package com.booking.booking.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

// 클라이언트의 로그인 요청 데이터(DTO)
@Data
public class LoginRequest {

    @NotBlank
    private String email;

    @NotBlank
    private String password;
}
