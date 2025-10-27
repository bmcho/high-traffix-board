package com.bmcho.hightrafficboard.advice;

import com.bmcho.hightrafficboard.controller.BoardApiResponse;
import com.bmcho.hightrafficboard.exception.BasicException;
import com.bmcho.hightrafficboard.exception.ErrorCode;
import com.bmcho.hightrafficboard.exception.UserException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.bmcho.hightrafficboard.exception.ErrorCode.*;


@Slf4j
@RestControllerAdvice
public class GlobalExceptionAdvice {

    @ExceptionHandler(UserException.class)
    protected BoardApiResponse<?> handleUserException(UserException userException) {
        log.error("error={}", userException.getMessage(), userException);
        ErrorCode errorCode = userException.getCode();
        return BoardApiResponse.fail(errorCode.getCode(), errorCode.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    protected BoardApiResponse<?> handleRuntimeException(RuntimeException runtimeException) {
        log.error("error={}", runtimeException.getMessage(), runtimeException);

        String errorMessage = runtimeException.getMessage();
        if (errorMessage == null) {
            errorMessage = DEFAULT_ERROR.getMessage();
        }

        return BoardApiResponse.fail(DEFAULT_ERROR.getCode(), errorMessage);
    }

    @ExceptionHandler(BadCredentialsException.class)
    protected BoardApiResponse<?> handleBadCredentialsException(BadCredentialsException e) {
        log.error("Authentication failed: {}", e.getMessage(), e);
        return BoardApiResponse.fail(AUTHENTICATION_FAILED.getCode(), AUTHENTICATION_FAILED.getMessage());
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    protected BoardApiResponse<?> handleAuthorizationDeniedException(AuthorizationDeniedException e) {
        log.error("AuthorizationDeniedException : {}", e.getMessage(), e);
        return BoardApiResponse.fail(ACCESS_DENIED.getCode(), ACCESS_DENIED.getMessage());
    }

    @ExceptionHandler(BasicException.class)
    protected BoardApiResponse<?> handleBasicException(BasicException exception) {
        log.error("Exception: {}", exception.getMsg(), exception);
        return BoardApiResponse.fail(exception.getCode().getCode(), exception.getMsg());
    }
}
