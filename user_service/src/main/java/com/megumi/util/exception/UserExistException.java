package com.megumi.util.exception;

public class UserExistException extends Exception {
    public UserExistException(String message) {
        super(message);
    }

    public UserExistException() {
        super("该账号已存在");
    }
}
