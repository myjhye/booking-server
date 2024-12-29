package com.booking.booking.controller;

import com.booking.booking.model.Board;
import com.booking.booking.model.User;
import com.booking.booking.request.BoardRequest;
import com.booking.booking.response.BoardResponse;
import com.booking.booking.security.user.HotelUserDetails;
import com.booking.booking.service.BoardService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/boards")
public class BoardController {

    private final BoardService boardService;

    // 게시글 생성
    @PostMapping("/add/new-board")
    @Transactional
    public ResponseEntity<BoardResponse> createBoard(@Valid @RequestBody BoardRequest request) {

        // 1. 현재 Security Context에서 인증 정보(로그인된 사용자의 인증 객체)를 가져옴
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof HotelUserDetails) {

            // 2. 인증 객체에서 principal(사용자 정보)를 가져와 HotelUserDetails로 형변환
            HotelUserDetails userDetails = (HotelUserDetails) authentication.getPrincipal();
            // 3. HotelUserDetails에서 User 정보 (작성자 정보) 가져옴
            User user = userDetails.getUser();

            Board board = new Board();
            board.setTitle(request.getTitle());
            board.setContent(request.getContent());
            board.setUser(user);
            board.setCreatedAt(LocalDateTime.now());

            Board savedBoard = boardService.createBoard(board);
            return ResponseEntity.ok(new BoardResponse(savedBoard));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }


    // 게시글 전체 조회
    @GetMapping("/all-boards")
    public ResponseEntity<List<BoardResponse>> getAllBoards() {

        List<Board> boards = boardService.getAllBoards();

        List<BoardResponse> boardResponses = boards.stream()
                                                   .map(BoardResponse::new)
                                                   .collect(Collectors.toList());

        return ResponseEntity.ok(boardResponses);
    }


    // 게시글 개별 조회
    @GetMapping("/{boardId}")
    public ResponseEntity<BoardResponse> getBoardById(@PathVariable Long boardId) {
        Board board = boardService.getBoardById(boardId);

        if (board != null) {
            return ResponseEntity.ok(new BoardResponse(board));
        }

        return ResponseEntity.notFound().build();
    }


    // 게시글 수정
    @PutMapping("/{boardId}")
    @Transactional
    public ResponseEntity<BoardResponse> updateBoard(
            @PathVariable Long boardId,
            @Valid @RequestBody BoardRequest request
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof HotelUserDetails) {

            HotelUserDetails userDetails = (HotelUserDetails) authentication.getPrincipal();
            User user = userDetails.getUser();

            // 2. 수정하려는 게시글 조회
            Board existingBoard = boardService.getBoardById(boardId);

            if (existingBoard == null) {
                return ResponseEntity.notFound().build();
            }

            // 3. 현재 사용자가 게시글 작성자인지 확인
            if (!existingBoard.getUser().getId().equals(user.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            // 4. 게시글 정보 업데이트
            existingBoard.setTitle(request.getTitle());
            existingBoard.setContent(request.getContent());

            Board updatedBoard = boardService.updatedBoard(existingBoard);

            return ResponseEntity.ok(new BoardResponse(updatedBoard));

        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}
