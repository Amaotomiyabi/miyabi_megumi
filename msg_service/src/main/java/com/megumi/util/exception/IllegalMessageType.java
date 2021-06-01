package com.megumi.util.exception;

public class IllegalMessageType extends Exception {
    public IllegalMessageType(String s) {
        super(s);
    }

    public IllegalMessageType() {
        super("错误的消息类型");
    }
}
