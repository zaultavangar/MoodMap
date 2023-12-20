package com.example.backend.caching;

import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.concurrent.TimeUnit;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for setting up caching in the application.
 * Enables caching and configures the cache manager with Caffeine.
 */
@Configuration
@EnableCaching
public class CacheConfig {

  /**
   * Creates and configures a CacheManager with Caffeine.
   *
   * @return A CacheManager instance for caching.
   */
  @Bean
  public CacheManager cacheManager(){
    CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager();
    caffeineCacheManager.setCaffeine(caffeineCacheBuilder());
    return caffeineCacheManager;
  }

  /**
   * Configures and builds a Caffeine cache.
   * Sets the maximum size and expiration time for the cache.
   *
   * @return A Caffeine cache builder.
   */
  @Bean
  public Caffeine<Object, Object> caffeineCacheBuilder() {
    int MAX_CACHE_SIZE = 1000;
    int EXPIRED_CACHE_TIME = 25;
    return Caffeine.newBuilder()
        .maximumSize(MAX_CACHE_SIZE)
        .expireAfterWrite(EXPIRED_CACHE_TIME, TimeUnit.HOURS)
        .recordStats();

  }

}
