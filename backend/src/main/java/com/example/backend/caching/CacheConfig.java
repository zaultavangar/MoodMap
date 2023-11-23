package com.example.backend.caching;

import com.example.backend.mapboxGeocodingService.GeoJson;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.concurrent.TimeUnit;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CacheConfig {
  private final int MAX_CACHE_SIZE = 100000;
  private final int EXPIRED_CACHE_TIME = 360;

  @Bean
  public CacheManager cacheManager(){
    CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager();
    caffeineCacheManager.setCaffeine(caffeineCacheBuilder());
    return caffeineCacheManager;
  }

  @Bean
  public Caffeine<Object, Object> caffeineCacheBuilder() {
    return Caffeine.newBuilder()
        .maximumSize(MAX_CACHE_SIZE)
        .expireAfterWrite(EXPIRED_CACHE_TIME, TimeUnit.MINUTES)
        .recordStats();

  }



//  @Bean
//  public Cache<String, Object> cache() {
//    return CacheBuilder.newBuilder()
//        .expireAfterWrite(EXPIRED_CACHE_TIME, TimeUnit.MINUTES)
//        .maximumSize(MAX_CACHE_SIZE)
//        .build();
//  }
}
