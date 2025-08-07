package com.bmcho.hightrafficboard.controller;

public record BoardApiResponse<T>(boolean success, String code, String message, T data) {
    public static final String CODE_SUCCEED = "0000";
    public static final String MESSAGE_SUCCEED = "Success";

    public static <T> BoardApiResponse<T> ok(T data) {
        return new BoardApiResponse<>(true, CODE_SUCCEED, MESSAGE_SUCCEED, data);
    }

    public static <T> BoardApiResponse<T> fail(String errorCode, String message) {
        return new BoardApiResponse<>(false, errorCode, message, null);
    }
}