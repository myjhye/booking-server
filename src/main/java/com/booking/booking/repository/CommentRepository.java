package com.booking.booking.repository;

import com.booking.booking.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByBoardIdOrderByCreatedAtDesc(Long boardId);
}
