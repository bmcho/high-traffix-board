package com.bmcho.hightrafficboard.exception;

import lombok.Getter;

@Getter
public class BasicException extends RuntimeException{

    private final ErrorCode code;
    private final String msg;

    public BasicException(ErrorCode code) {
        this.code = code;
        this.msg = code.getMessage();
    }

    public BasicException(String msg) {
        this.code = ErrorCode.DEFAULT_ERROR;
        this.msg = msg;
    }
}
