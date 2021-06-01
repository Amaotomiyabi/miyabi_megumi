package com.megumi.aspect;

import com.megumi.aspect.annotation.RequireValid;
import com.megumi.common.StringUtils;
import com.megumi.util.exception.IllegalRequestParameterException;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * 2021/2/25
 *
 * @author miyabi
 * @since 1.0
 */
@Aspect
@Component
public class ValidateArgs {

    @Pointcut("execution(* com.megumi.service..*.*(..))")
    public void validateAspect() {
    }

    @Before("validateAspect()")
    public void beforeAdvice(JoinPoint pjp) throws IllegalRequestParameterException {
        var clazz = pjp.getSignature().getDeclaringType();
        var methods = clazz.getDeclaredMethods();
        var flag = false;
        for (Method method : methods) {
            if (method.getName().equalsIgnoreCase(pjp.getSignature().getName())) {
                if (method.getAnnotation(RequireValid.class) != null) {
                    flag = true;
                }
                break;
            }
        }
        if (flag) {
            var args = pjp.getArgs();
            for (Object arg : args) {
                if (arg == null) {
                    throw new IllegalRequestParameterException();
                }
                if (arg instanceof String s) {
                    if (!StringUtils.isValid(s)) {
                        throw new IllegalRequestParameterException("无效的参数(字符串)");
                    }
                }
            }
        }
    }
}
