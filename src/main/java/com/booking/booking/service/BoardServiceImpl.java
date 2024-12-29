package com.booking.booking.service;

import com.booking.booking.model.Board;
import com.booking.booking.repository.BoardRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class BoardServiceImpl implements BoardService {

    private final BoardRepository boardRepository;

    // 게시글 생성
    @Override
    public Board createBoard(Board board) {
        return boardRepository.save(board);
    }

    // 게시글 전체 조회
    @Override
    public List<Board> getAllBoards() {
        return boardRepository.findAll();
    }

    // 게시글 개별 조회
    @Override
    public Board getBoardById(Long boardId) {
        return boardRepository.findById(boardId)
                .orElse(null);
    }

    // 게시글 수정
    @Override
    public Board updatedBoard(Board existingBoard) {
        return boardRepository.save(existingBoard);
    }
}
