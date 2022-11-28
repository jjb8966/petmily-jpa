package com.petmily.exception;

public class PasswordIncorrectException extends RuntimeException {

    public PasswordIncorrectException() {
    }

    public PasswordIncorrectException(String message) {
        super(message);
    }

}
