package com.booking.booking.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BoardRequest {

    @NotBlank
    private String title;
    @NotBlank
    private String content;
}
