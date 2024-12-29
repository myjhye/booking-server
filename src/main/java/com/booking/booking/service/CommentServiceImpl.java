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


}
