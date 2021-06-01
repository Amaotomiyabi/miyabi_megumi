package com.megumi.util.exception;

public class ValidateCodeError extends Exception {
    public ValidateCodeError(String message) {
        super(message);
    }

    public ValidateCodeError() {
        super("验证码错误或失效");
    }
}
