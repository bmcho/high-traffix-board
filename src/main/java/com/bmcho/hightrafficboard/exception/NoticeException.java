package com.bmcho.hightrafficboard.exception;

import lombok.Getter;

@Getter
public class NoticeException extends BasicException {

    public NoticeException(ErrorCode code) {
        super(code);
    }

    public static class NoticeDoesNotExistException extends NoticeException {
        public NoticeDoesNotExistException() {
            super(ErrorCode.NOTICE_DOES_NOT_EXIST);
        }
    }

}
