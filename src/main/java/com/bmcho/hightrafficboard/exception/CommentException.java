package com.bmcho.hightrafficboard.exception;

public class CommentException extends BasicException {

    public CommentException(ErrorCode code) {
        super(code);
    }

    public static class CommentDoesNotExistException extends CommentException {
        public CommentDoesNotExistException() {
            super(ErrorCode.COMMENT_DOES_NOT_EXIST);
        }
    }

    public static class CommentNotEditedByRateLimitException extends CommentException {
        public CommentNotEditedByRateLimitException() {
            super(ErrorCode.COMMENT_NOT_EDITED_BY_RATE_LIMIT);
        }
    }

    public static class CommentAuthorDifferentException extends CommentException {
        public CommentAuthorDifferentException() {
            super(ErrorCode.COMMENT_AUTHOR_DIFFERENT);
        }
    }

}
