package com.fenil.projecthub.common.exception;

import com.fenil.projecthub.auth.exception.AccountUnavailableException;
import com.fenil.projecthub.auth.exception.EmailAlreadyExistsException;
import com.fenil.projecthub.auth.exception.InvalidCredentialsException;
import com.fenil.projecthub.auth.exception.InvalidRefreshTokenException;
import com.fenil.projecthub.user.exception.UserNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiError> handleUserNotFound(
            UserNotFoundException exception,
            HttpServletRequest request
    ) {
        ErrorCode errorCode = ErrorCode.USER_NOT_FOUND;

        ApiError error = ApiError.of(
                errorCode.getStatus().value(),
                errorCode.getStatus().getReasonPhrase(),
                errorCode.getCode(),
                exception.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(errorCode.getStatus()).body(error);


    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleMethodArgumentNotValid(
            MethodArgumentNotValidException exception,
            HttpServletRequest request
    ) {
        List<ApiError.FieldViolation> violations = exception.getBindingResult().getFieldErrors().stream().map(this::toFieldViolation).toList();

        ErrorCode errorCode = ErrorCode.VALIDATION_ERROR;

        ApiError error = new ApiError(
                java.time.Instant.now(),
                errorCode.getStatus().value(),
                errorCode.getStatus().getReasonPhrase(),
                errorCode.getCode(),
                "Request validation failed",
                request.getRequestURI(),
                violations
        );
        return ResponseEntity.status(errorCode.getStatus()).body(error);


    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraintViolation(
            ConstraintViolationException exception,
            HttpServletRequest request
    ) {
        List<ApiError.FieldViolation> violations = exception.getConstraintViolations().stream().map(violation -> new ApiError.FieldViolation(
                violation.getPropertyPath().toString(),
                violation.getMessage()
        )).toList();

        ErrorCode errorCode = ErrorCode.VALIDATION_ERROR;
        ApiError error = new ApiError(
                java.time.Instant.now(),
                errorCode.getStatus().value(),
                errorCode.getStatus().getReasonPhrase(),
                errorCode.getCode(),
                "Request validation failed",
                request.getRequestURI(),
                violations
        );

        return ResponseEntity.status(errorCode.getStatus()).body(error);

    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleUnreadableMessage(
            HttpMessageNotReadableException exception,
            HttpServletRequest request
    ) {
        ErrorCode errorCode = ErrorCode.INVALID_REQUEST;

        ApiError error = ApiError.of(
                errorCode.getStatus().value(),
                errorCode.getStatus().getReasonPhrase(),
                errorCode.getCode(),
                "The request is missong or malformed",
                request.getRequestURI()
        );

        return ResponseEntity.status(errorCode.getStatus()).body(error);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> handleTypeMismatch(
            MethodArgumentTypeMismatchException exception,
            HttpServletRequest request
    ) {

        ErrorCode errorCode = ErrorCode.INVALID_REQUEST;

        String message = "Invalid request parameter" + exception.getName();

        ApiError error = ApiError.of(
                errorCode.getStatus().value(),
                errorCode.getStatus().getReasonPhrase(),
                errorCode.getCode(),
                message,
                request.getRequestURI()
        );

        return ResponseEntity
                .status(errorCode.getStatus())
                .body(error);
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ApiError> handleEmailAlreadyExists(
            EmailAlreadyExistsException exception,
            HttpServletRequest request
    ) {
        ErrorCode errorCode = ErrorCode.EMAIL_ALREADY_EXISTS;

        ApiError error = ApiError.of(
                errorCode.getStatus().value(),
                errorCode.getStatus().getReasonPhrase(),
                errorCode.getCode(),
                exception.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity
                .status(errorCode.getStatus())
                .body(error);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ApiError> handleInvalidCredentials(
            InvalidCredentialsException exception,
            HttpServletRequest request
    ) {
        ErrorCode errorCode = ErrorCode.INVALID_CREDENTIALS;

        ApiError error = ApiError.of(
                errorCode.getStatus().value(),
                errorCode.getStatus().getReasonPhrase(),
                errorCode.getCode(),
                exception.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity
                .status(errorCode.getStatus())
                .body(error);
    }

    @ExceptionHandler(AccountUnavailableException.class)
    public ResponseEntity<ApiError> handleAccountUnavailable(
            AccountUnavailableException exception,
            HttpServletRequest request
    ) {
        ErrorCode errorCode = ErrorCode.ACCOUNT_UNAVAILABLE;

        ApiError error = ApiError.of(
                errorCode.getStatus().value(),
                errorCode.getStatus().getReasonPhrase(),
                errorCode.getCode(),
                exception.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity
                .status(errorCode.getStatus())
                .body(error);
    }

    @ExceptionHandler(InvalidRefreshTokenException.class)
    public ResponseEntity<ApiError> handleInvalidRefreshToken(
            InvalidRefreshTokenException exception,
            HttpServletRequest request
    ) {
        ErrorCode errorCode =
                ErrorCode.INVALID_REFRESH_TOKEN;

        ApiError error = ApiError.of(
                errorCode.getStatus().value(),
                errorCode.getStatus().getReasonPhrase(),
                errorCode.getCode(),
                exception.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity
                .status(errorCode.getStatus())
                .body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleUnexpectedException(
            Exception exception,
            HttpServletRequest request
    ) {

        log.error(
                "Unhandled exception while processing {} {}",
                request.getMethod(),
                request.getRequestURI(),
                exception
        );
        ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;

        ApiError error = ApiError.of(
                errorCode.getStatus().value(),
                errorCode.getStatus().getReasonPhrase(),
                errorCode.getCode(),
                "An unexpected error occurred",
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    private ApiError.FieldViolation toFieldViolation(
            FieldError fieldError
    ) {
        return new ApiError.FieldViolation(
                fieldError.getField(),
                fieldError.getDefaultMessage() == null
                        ? "Invalid value"
                        : fieldError.getDefaultMessage()
        );
    }
}
