package com.bmcho.hightrafficboard.service;

import com.bmcho.hightrafficboard.entity.BoardEntity;
import com.bmcho.hightrafficboard.exception.BoardException;
import com.bmcho.hightrafficboard.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;

    public BoardEntity getBoard(Long boardId) {
        return boardRepository.findById(boardId)
            .orElseThrow(BoardException.BoardDoesNotExistException::new);
    }
}
