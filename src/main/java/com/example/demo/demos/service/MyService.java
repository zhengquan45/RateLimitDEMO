package com.example.demo.demos.service;

import com.example.demo.anno.RateLimited;
import org.springframework.stereotype.Service;

@Service
public class MyService {


    @RateLimited(limitForPeriod = 3, limitRefreshPeriod = 20, fallback = "fallbackMethod")
    public String limitedMethod() {
        return "请求执行";
    }

    public String fallbackMethod() {
        return "被限流了";
    }
}