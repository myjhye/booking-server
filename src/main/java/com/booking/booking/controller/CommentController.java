package com.booking.booking.controller;

import com.booking.booking.model.Comment;
import com.booking.booking.model.User;
import com.booking.booking.request.CreateCommentRequest;
import com.booking.booking.request.UpdateCommentRequest;
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
    public ResponseEntity<CommentResponse> createComment(@Valid @RequestBody CreateCommentRequest request) {

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


    // 댓글 수정
    @PutMapping("/{commentId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<CommentResponse> updateComment(
            @PathVariable Long commentId,
            @Valid @RequestBody UpdateCommentRequest request
    ) {
        // 1. 현재 인증된 사용자 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof HotelUserDetails) {

            HotelUserDetails userDetails = (HotelUserDetails) authentication.getPrincipal();
            User user = userDetails.getUser();

            // 2. 수정하려는 댓글 조회
            Comment existingComment = commentService.getCommentById(commentId);
            

            // 3. 수정하려는 댓글이 존재하지 않는 경우
            if (existingComment == null) {
                return ResponseEntity.notFound().build();
            }

            // 4. 댓글 작성자와 현재 로그인한 사용자가 일치하는지 확인
            if (!existingComment.getUser().getId().equals(user.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            // 5. 댓글 내용 업데이트
            existingComment.setContent(request.getContent());

            // 6. 댓글 저장
            Comment updatedComment = commentService.updateComment(existingComment);

            // 7. 응답 반환
            return ResponseEntity.ok(new CommentResponse(updatedComment));
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }


    // 댓글 삭제
    @DeleteMapping("/{commentId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof HotelUserDetails) {
            HotelUserDetails userDetails = (HotelUserDetails) authentication.getPrincipal();
            User user = userDetails.getUser();

            // 2. 삭제하려는 댓글 조회
            Comment existingComment = commentService.getCommentById(commentId);

            if (existingComment == null) {
                return ResponseEntity.notFound().build();
            }

            // 3. 현재 사용자가 댓글 작성자인지 확인
            if (!existingComment.getUser().getId().equals(user.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            // 4. 댓글 삭제
            commentService.deleteComment(commentId);

            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

}
