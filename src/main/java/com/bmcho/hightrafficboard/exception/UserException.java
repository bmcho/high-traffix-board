package com.bmcho.hightrafficboard.exception;

import lombok.Getter;

@Getter
public class UserException extends BasicException {

    public UserException(ErrorCode code) {
        super(code);
    }

    public static class UserAlreadyExistException extends UserException {
        public UserAlreadyExistException() {
            super(ErrorCode.USER_ALREADY_EXIST);
        }
    }

    public static class UserDoesNotExistException extends UserException {
        public UserDoesNotExistException() {
            super(ErrorCode.USER_DOES_NOT_EXIST);
        }
    }

}
