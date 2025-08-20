package com.bmcho.hightrafficboard.exception;

import org.springframework.http.HttpStatus;

public class BoardException extends BasicException {

    public BoardException(ErrorCode code) {
        super(code);
    }

    public static class BoardDoesNotExistException extends BoardException {
        public BoardDoesNotExistException() {
            super(ErrorCode.BOARD_DOES_NOT_EXIST);
        }
    }
}
