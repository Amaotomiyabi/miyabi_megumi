package com.megumi.aspect.annotation;

import java.lang.annotation.*;

/**
 * 2021/2/25
 *
 * @author miyabi
 * @since 1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface RequireValid {
}
