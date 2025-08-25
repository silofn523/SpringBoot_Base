package com.example.weesh.data.redis;

import java.time.Duration;

public interface RedisService {
    void setValues(String key, String value, Duration timeout);
    String getValues(String key);
    void deleteValues(String key);
}