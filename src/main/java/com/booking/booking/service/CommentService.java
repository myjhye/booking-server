package com.booking.booking.service;

import com.booking.booking.model.Comment;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CommentService {

    // 댓글 작성
    Comment createComment(Comment comment);

    // 댓글 조회 (게시글별)
    List<Comment> getCommentsByBoardId(Long boardId);

    // 댓글 개별 조회
    Comment getCommentById(Long commentId);

    // 댓글 수정
    Comment updateComment(Comment existingComment);

    // 댓글 삭제
    void deleteComment(Long commentId);
}
