package com.bmcho.hightrafficboard.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    DEFAULT_ERROR("CODE0000", "에러가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),

    PASSWORD_ENCRYPTION_FAILED("CODE1000", "비밀번호 암호화 중 에러가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    USER_ALREADY_EXIST("CODE2000", "사용자가 이미 존재합니다.", HttpStatus.CONFLICT), // 409
    USER_DOES_NOT_EXIST("CODE2001", "사용자가 존재하지 않습니다.", HttpStatus.NOT_FOUND), // 404
    AUTHENTICATION_FAILED("CODE2002", "인증에 실패했습니다. 이메일 또는 비밀번호를 확인해주세요.", HttpStatus.UNAUTHORIZED), // 401

    ACCESS_DENIED("CODE2004", "해당 기능에 접근이 제한됩니다.", HttpStatus.FORBIDDEN), // 403

    BOARD_DOES_NOT_EXIST("CODE3001", "게시판이 존재하지 않습니다.", HttpStatus.NOT_FOUND), // 404

    ARTICLE_DOES_NOT_EXIST("CODE4001", "게시글이 존재하지 않습니다.", HttpStatus.NOT_FOUND), // 404
    ARTICLE_NOT_EDITED_BY_RATE_LIMIT("CODE4002", "수정 요청이 너무 잦아 잠시 후 다시 시도해주세요.", HttpStatus.TOO_MANY_REQUESTS), // 429
    ARTICLE_AUTHOR_DIFFERENT("CODE4002", "작성자가 아닙니다.", HttpStatus.FORBIDDEN), // 429
    ;


    private final String code;
    private final String message;
    private final HttpStatus status;

    ErrorCode(String code, String message, HttpStatus status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }

    @Override
    public String toString() {
        return String.format("%s: [%s] %s", this.status, this.code, this.message);
    }
}
