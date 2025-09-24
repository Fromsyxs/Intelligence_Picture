package com.feng.yupicturebackend.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author       : 缘在风里
 * @createDate   : 2025/9/8 23:35
 */
// 目标为方法
@Target(ElementType.METHOD)
// 运行时生效
@Retention(RetentionPolicy.RUNTIME)
public @interface AuthCheck {
    /**
     * 必须有某个角色
     */
    String mustRole() default "";
}
