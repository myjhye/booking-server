package com.booking.booking.controller;

import com.booking.booking.model.Comment;
import com.booking.booking.model.User;
import com.booking.booking.request.CommentRequest;
import com.booking.booking.response.BoardResponse;
import com.booking.booking.response.CommentResponse;
import com.booking.booking.security.user.HotelUserDetails;
import com.booking.booking.service.CommentService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/comments")
public class CommentController {

    private final CommentService commentService;

    // 댓글 작성
    @PostMapping("/add/new-comment")
    @Transactional
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<CommentResponse> createComment(@Valid @RequestBody CommentRequest request) {

        // 1. 현재 Security Context에서 인증 정보(로그인된 사용자의 인증 객체)를 가져옴
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof HotelUserDetails) {

            // 2. 인증 객체에서 principal(사용자 정보)를 가져와 HotelUserDetails로 형변환
            HotelUserDetails userDetails = (HotelUserDetails) authentication.getPrincipal();
            // 3. HotelUserDetails에서 User 정보 (작성자 정보) 가져옴
            User user = userDetails.getUser();

            Comment comment = new Comment();
            comment.setContent(request.getContent());
            comment.setUser(user);
            comment.setBoard(request.getBoard());
            comment.setCreatedAt(LocalDateTime.now());

            Comment savedComment = commentService.createComment(comment);

            return ResponseEntity.ok(new CommentResponse(savedComment));
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }


    // 댓글 조회 (게시글별)
    @GetMapping("/board/{boardId}")
    public ResponseEntity<List<CommentResponse>> getCommentsByBoardId(@PathVariable Long boardId) {

        List<Comment> comments = commentService.getCommentsByBoardId(boardId);

        List<CommentResponse> commentResponses = comments.stream()
                                                         .map(CommentResponse::new)
                                                         .collect(Collectors.toList());

        return ResponseEntity.ok(commentResponses);
    }
}
