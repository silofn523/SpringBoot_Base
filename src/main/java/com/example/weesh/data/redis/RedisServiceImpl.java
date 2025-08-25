package com.example.weesh.data.redis;

import com.example.weesh.core.foundation.log.LoggingUtil;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RedisServiceImpl implements RedisService {

    private final RedisTemplate<String, String> redisTemplate;

    @PostConstruct
    public void checkConnection() {
        try {
            redisTemplate.getConnectionFactory().getConnection().ping();
            LoggingUtil.info("Redis connection is active");
        } catch (Exception e) {
            LoggingUtil.error("Redis connection failed, error: {}", e.getMessage());
            throw new RuntimeException("Redis connection check failed", e);
        }
    }

    @Override
    public void setValues(String key, String value, Duration timeout) {
        try {
            redisTemplate.opsForValue().set(key, value, timeout);
            LoggingUtil.info("Redis set value successfully for key: {}", key);
        } catch (Exception e) {
            LoggingUtil.error("Failed to set value in Redis for key: {}, error: {}", key, e.getMessage());
            throw new RuntimeException("Redis operation failed", e);
        }
    }

    @Override
    public String getValues(String key) {
        try {
            String value = redisTemplate.opsForValue().get(key);
            LoggingUtil.info("Redis get value successfully for key: {}, value: {}", key, value);
            return value;
        } catch (Exception e) {
            LoggingUtil.error("Failed to get value from Redis for key: {}, error: {}", key, e.getMessage());
            throw new RuntimeException("Redis operation failed", e);
        }
    }

    @Override
    public void deleteValues(String key) {
        try {
            Boolean deleted = redisTemplate.delete(key);
            if (deleted) {
                LoggingUtil.info("Redis deleted value successfully for key: {}", key);
            } else {
                LoggingUtil.warn("Redis delete operation had no effect for key: {}", key);
            }
        } catch (Exception e) {
            LoggingUtil.error("Failed to delete value from Redis for key: {}, error: {}", key, e.getMessage());
            throw new RuntimeException("Redis operation failed", e);
        }
    }
}
