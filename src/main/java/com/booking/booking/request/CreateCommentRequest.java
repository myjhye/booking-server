package com.booking.booking.request;

import com.booking.booking.model.Board;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateCommentRequest {

    @NotBlank
    private String content;
    @NotNull
    private Board board;
}
