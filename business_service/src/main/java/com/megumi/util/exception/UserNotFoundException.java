package com.megumi.util.exception;

/**
 * 2021/2/27
 *
 * @author miyabi
 * @since 1.0
 */
public class UserNotFoundException extends Exception {

    public UserNotFoundException(String message) {
        super(message);
    }

    public UserNotFoundException() {
        super("未找到该用户");
    }
}
