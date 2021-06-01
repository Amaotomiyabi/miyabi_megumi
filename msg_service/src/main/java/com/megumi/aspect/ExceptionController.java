package com.megumi.aspect;

import com.megumi.common.ResultBody;
import com.megumi.common.StateCode;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.lang.reflect.UndeclaredThrowableException;

@RestControllerAdvice
public class ExceptionController {

    @ExceptionHandler(value = Throwable.class)
    public ResultBody<String> exceptionHandle(Throwable e) {
        if (e instanceof UndeclaredThrowableException) {
            e = e.getCause();
        }
        e.printStackTrace();
        return new ResultBody<>(StateCode.SEVER_ERR, e.getMessage());
    }
}
