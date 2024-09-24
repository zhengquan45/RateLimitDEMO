package com.example.demo.aop;

import com.example.demo.anno.RateLimited;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import io.netty.util.internal.StringUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.function.Supplier;

@Aspect
@Component
public class RateLimitingAspect {

    private final RateLimiterRegistry rateLimiterRegistry;

    @Autowired
    public RateLimitingAspect(RateLimiterRegistry rateLimiterRegistry) {
        this.rateLimiterRegistry = rateLimiterRegistry;
    }

    @Around("@annotation(rateLimited)")
    public Object rateLimit(ProceedingJoinPoint joinPoint, RateLimited rateLimited) throws Throwable {
        RateLimiterConfig config = RateLimiterConfig.custom()
                .limitForPeriod(rateLimited.limitForPeriod())
                .limitRefreshPeriod(Duration.ofSeconds(rateLimited.limitRefreshPeriod()))
                .build();


        String className = joinPoint.getTarget().getClass().getName();
        String method = joinPoint.getSignature().getName();
        String key = rateLimited.value().isEmpty() ?"rateLimit:" + className + ":" + method:rateLimited.value();
        RateLimiter rateLimiter = rateLimiterRegistry.rateLimiter(key, config);

        if (rateLimiter.acquirePermission()) {
            try {
                return joinPoint.proceed();
            } catch (Throwable throwable) {
                return handleFallback(joinPoint, rateLimited.fallback(), throwable);
            }
        }else{
            return blockHandle(joinPoint, rateLimited.fallback(), new RuntimeException("被限流了"));
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

    private Object blockHandle(ProceedingJoinPoint joinPoint, String blockHandleMethodName, Throwable throwable) {
        if (!blockHandleMethodName.isEmpty()) {
            try {
                // 获取目标类
                Object target = joinPoint.getTarget();
                // 获取 blockHandle 方法
                return target.getClass().getMethod(blockHandleMethodName).invoke(target);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        throw new RuntimeException(throwable);
    }
}