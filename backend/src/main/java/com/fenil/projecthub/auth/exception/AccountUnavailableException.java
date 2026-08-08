package com.fenil.projecthub.auth.exception;

public class AccountUnavailableException extends RuntimeException {
    public AccountUnavailableException() {
        super("This account is not currently available");
    }
}
