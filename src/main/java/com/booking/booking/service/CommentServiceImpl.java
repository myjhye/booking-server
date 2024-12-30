package com.booking.booking.service;

import com.booking.booking.model.Comment;
import com.booking.booking.repository.CommentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    // 댓글 생성
    @Override
    public Comment createComment(Comment comment) {
        return commentRepository.save(comment);
    }

    // 댓글 조회 (게시글별)
    @Override
    public List<Comment> getCommentsByBoardId(Long boardId) {
        return commentRepository.findByBoardIdOrderByCreatedAtDesc(boardId);
    }

    // 댓글 개별 조회
    @Override
    public Comment getCommentById(Long commentId) {
        return commentRepository.findById(commentId)
                .orElse(null);
    }

    // 댓글 수정
    @Override
    public Comment updateComment(Comment existingComment) {
        return commentRepository.save(existingComment);
    }

    // 댓글 삭제
    @Override
    public void deleteComment(Long commentId) {
        commentRepository.deleteById(commentId);
    }


}
