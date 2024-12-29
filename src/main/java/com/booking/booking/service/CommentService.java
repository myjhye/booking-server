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
}
