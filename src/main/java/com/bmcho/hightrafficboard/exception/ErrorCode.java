package com.bmcho.hightrafficboard.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {

    DEFAULT_ERROR("CODE0000", "에러가 발생했습니다."),
    PASSWORD_ENCRYPTION_FAILED("CODE1000", "비밀번호 암호화 중 에러가 발생했습니다."),
    USER_ALREADY_EXIST("CODE2000", "사용자가 이미 존재합니다."),
    USER_DOES_NOT_EXIST("CODE2001", "사용자가 존재하지 않습니다."),
    AUTHENTICATION_FAILED("CODE2002", "인증에 실패했습니다. 이메일 또는 비밀번호를 확인해주세요."),
    ACCESS_DENIED("CODE2004", "해당 기능에 접근이 제한됩니다.")
    ;

    private final String code;
    private final String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s", this.code, this.message);
    }
}
