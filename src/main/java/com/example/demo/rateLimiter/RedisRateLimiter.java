package com.example.demo.rateLimiter;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class RedisRateLimiter {

    private final StringRedisTemplate redisTemplate;

    public RedisRateLimiter(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public boolean tryAcquire(String key, int limit, int period) {
        long currentTime = Instant.now().getEpochSecond();
        long windowStart = currentTime - period;

        // 清理过期请求计数
        redisTemplate.opsForZSet().removeRangeByScore(key, 0, windowStart);

        long requestCount = redisTemplate.opsForZSet().count(key, windowStart, currentTime) == null ? 0 : redisTemplate.opsForZSet().count(key, windowStart, currentTime).longValue();

        if (requestCount < limit) {
            redisTemplate.opsForZSet().add(key, String.valueOf(currentTime), currentTime);
            return true;
        }
        return false;
    }
}