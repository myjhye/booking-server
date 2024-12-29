package com.booking.booking.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class JwtRequest {

    @NotBlank
    private String email;
    @NotBlank
    private String password;
}
