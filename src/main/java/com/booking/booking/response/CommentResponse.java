package com.booking.booking.response;

import com.booking.booking.model.Comment;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentResponse {

    private Long id;
    private String content;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    private Long boardId;
    // 작성자 정보 (Comment 응답 반환 시 해당 게시글의 작성자 정보도 함께 반환)
    private UserResponse user;

    // 엔티티 -> DTO 변환 생성자
    public CommentResponse(Comment comment) {
        this.id = comment.getId();
        this.content = comment.getContent();
        this.createdAt = comment.getCreatedAt();
        this.boardId = comment.getBoard().getId();
        // User 엔티티도 DTO로 변환
        this.user = new UserResponse(
            comment.getUser().getId(),
            comment.getUser().getEmail(),
            comment.getUser().getFirstName(),
            comment.getUser().getLastName()
        );
    }

}
