package com.megumi.util.exception;

/**
 * 错误的请求参数异常
 * <p>
 * 2021/3/8
 *
 * @author miyabi
 * @since 1.0
 */
public class IllegalRequestParameterException extends Exception {
    public IllegalRequestParameterException(String message) {
        super(message);
    }

    public IllegalRequestParameterException() {
        super("错误的请求参数");
    }
}
