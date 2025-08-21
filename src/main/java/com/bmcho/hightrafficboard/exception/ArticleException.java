package com.bmcho.hightrafficboard.exception;

import org.springframework.http.HttpStatus;

public class ArticleException extends BasicException {

    public ArticleException(ErrorCode code) {
        super(code);
    }

    public static class ArticleDoesNotExistException extends ArticleException {
        public ArticleDoesNotExistException() {
            super(ErrorCode.ARTICLE_DOES_NOT_EXIST);
        }
    }

    public static class ArticleNotEditedByRateLimitException extends ArticleException {
        public ArticleNotEditedByRateLimitException() {
            super(ErrorCode.ARTICLE_NOT_EDITED_BY_RATE_LIMIT);
        }
    }

    public static class ArticleAuthorDifferentException extends ArticleException {
        public ArticleAuthorDifferentException() {
            super(ErrorCode.ARTICLE_AUTHOR_DIFFERENT);
        }
    }

}
