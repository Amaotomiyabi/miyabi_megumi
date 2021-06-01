package com.megumi.util.exception;

public class EmailValidateError extends Exception {
    public EmailValidateError(String message) {
        super(message);
    }

    public EmailValidateError() {
        super("邮箱验证失败");
    }
}
