package com.fenil.projecthub.common.exception;

import java.time.Instant;
import java.util.List;

public record ApiError(
        Instant timestamp,
        int status,
        String error,
        String code,
        String message,
        String path,
        List<FieldViolation> violations
) {

    public record FieldViolation(
            String field,
            String message
    ) {
    }

    public static ApiError of(
            int status,
            String error,
            String code,
            String message,
            String path
    ) {
        return new ApiError(
                Instant.now(),
                status,
                error,
                code,
                message,
                path,
                List.of()
        );
    }
}