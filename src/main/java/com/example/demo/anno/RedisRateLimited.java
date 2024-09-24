package com.example.demo.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RedisRateLimited {
    String value() default "default"; // 限流器名称
    int limitForPeriod() default 10; // 每个周期的请求限制
    int limitRefreshPeriod() default 1; // 周期时长（秒）
    String fallback() default ""; // 回退方法名称
}