package com.miyabi.reptile;

/**
 * 超出最新Url异常
 *
 * @author miyabi
 * @date 2021-04-07-10-08
 * @since 1.0
 **/


public class MaxUrlException extends Exception {
    public MaxUrlException(String msg) {
        super(msg);
    }
}
