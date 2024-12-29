package com.booking.booking.service;

import com.booking.booking.model.Board;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface BoardService {

    // 게시글 생성
    public Board createBoard(Board board);

    // 게시글 전체 조회
    List<Board> getAllBoards();

    // 게시글 개별 조회
    Board getBoardById(Long boardId);

    //  게시글 수정
    Board updatedBoard(Board existingBoard);

    // 게시글 삭제
    void deleteBoard(Long boardId);
}
