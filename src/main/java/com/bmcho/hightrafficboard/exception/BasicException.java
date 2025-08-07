package com.bmcho.hightrafficboard.exception;

import lombok.Getter;

@Getter
public class BasicException extends RuntimeException{

    private final ErrorCode code;

    public BasicException(ErrorCode code) {
        this.code = code;
    }
}
