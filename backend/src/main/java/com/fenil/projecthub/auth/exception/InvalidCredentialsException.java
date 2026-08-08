package com.fenil.projecthub.auth.exception;

public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException() {
        super("Email or Password is invalid");
    }
}
