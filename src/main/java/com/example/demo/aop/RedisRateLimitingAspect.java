package com.example.demo.aop;

import com.example.demo.anno.RedisRateLimited;
import com.example.demo.rateLimiter.RedisRateLimiter;
import lombok.AllArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@AllArgsConstructor
public class RedisRateLimitingAspect {

    private final RedisRateLimiter redisRateLimiter;

    @Around("@annotation(rateLimited)")
    public Object rateLimit(ProceedingJoinPoint joinPoint, RedisRateLimited rateLimited) throws Throwable {
        String className = joinPoint.getTarget().getClass().getName();
        String method = joinPoint.getSignature().getName();
        String key = rateLimited.value().isEmpty() ?"rateLimit:" + className + ":" + method:rateLimited.value();
        if (redisRateLimiter.tryAcquire(key, rateLimited.limitForPeriod(), rateLimited.limitRefreshPeriod())) {
            try {
                return joinPoint.proceed();
            } catch (Exception e) {
                e.printStackTrace();
                return handleFallback(joinPoint, rateLimited.fallback(), new RuntimeException(e.getMessage()));
            }
        } else {
            return handleFallback(joinPoint, rateLimited.fallback(), new RuntimeException("Rate limit exceeded"));
        }
    }

    private Object handleFallback(ProceedingJoinPoint joinPoint, String fallbackMethodName, Throwable throwable) {
        if (!fallbackMethodName.isEmpty()) {
            try {
                // 获取目标类
                Object target = joinPoint.getTarget();
                // 获取 fallback 方法
                return target.getClass().getMethod(fallbackMethodName).invoke(target);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        throw new RuntimeException(throwable);
    }
}