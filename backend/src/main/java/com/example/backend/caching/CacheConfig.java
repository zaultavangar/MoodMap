package com.example.backend.caching;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class CacheConfig {

    int MAX_CACHE_SIZE = 100000;

    int EXPIRED_CACHE_TIME = 360;
    @Bean
    public Cache<String, Object> cache() {
        return CacheBuilder.newBuilder()
                .expireAfterWrite(EXPIRED_CACHE_TIME, TimeUnit.MINUTES)
                .maximumSize(MAX_CACHE_SIZE)
                .build();
    }
}
