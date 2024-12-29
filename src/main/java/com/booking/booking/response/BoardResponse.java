package com.booking.booking.response;

import com.booking.booking.model.Board;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BoardResponse {

    private Long id;
    private String title;
    private String content;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    // 작성자 정보 (Board 응답 반환 시 해당 게시글의 작성자 정보도 함께 반환)
    private UserResponse user;


    // 엔티티 -> DTO 변환 생성자
    public BoardResponse(Board board) {
        this.id = board.getId();
        this.title = board.getTitle();
        this.content = board.getContent();
        this.createdAt = board.getCreatedAt();
        // User 엔티티도 DTO로 변환
        this.user = new UserResponse(
            board.getUser().getId(),
            board.getUser().getEmail(),
            board.getUser().getFirstName(),
            board.getUser().getLastName()
        );
    }

}
