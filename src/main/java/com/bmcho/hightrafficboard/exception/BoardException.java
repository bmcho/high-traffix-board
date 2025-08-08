package com.bmcho.hightrafficboard.exception;

public class BoardException extends BasicException {

    public BoardException(ErrorCode code) {
        super(code);
    }

    public static class BoardDoesNotExistException extends BoardException {
        public BoardDoesNotExistException() {
            super(ErrorCode.USER_DOES_NOT_EXIST);
        }
    }
}
