package com.abbvmk.sathi.User;


public class UserValidationException extends Exception {
    public UserValidationException() {
    }

    public UserValidationException(String message) {
        super(message);
    }
}