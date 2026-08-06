package com.fenil.projecthub.common.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

    USER_NOT_FOUND(
            "USER_NOT_FOUND",
            HttpStatus.NOT_FOUND
    ),

    EMAIL_ALREADY_EXISTS(
            "EMAIL_ALREADY_EXISTS",
            HttpStatus.CONFLICT
    ),

    VALIDATION_ERROR(
            "VALIDATION_ERROR",
            HttpStatus.BAD_REQUEST
    ),

    INVALID_REQUEST(
            "INVALID_REQUEST",
            HttpStatus.BAD_REQUEST
    ),

    INTERNAL_SERVER_ERROR(
            "INTERNAL_SERVER_ERROR",
            HttpStatus.INTERNAL_SERVER_ERROR
    );

    private final String code;
    private final HttpStatus status;

    ErrorCode(
            String code,
            HttpStatus status
    ) {
        this.code = code;
        this.status = status;
    }

    public String getCode() {
        return code;
    }

    public HttpStatus getStatus() {
        return status;
    }
}