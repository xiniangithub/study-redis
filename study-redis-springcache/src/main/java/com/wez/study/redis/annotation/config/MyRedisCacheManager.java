package com.wez.study.redis.annotation.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * 自定义Redis缓存管理类
 * 说明：使用自定义Redis缓存管理类的方式，则会代替application.yml中的Redis配置。
 */
@Configuration
public class MyRedisCacheManager extends CachingConfigurerSupport {

    private static final Logger logger = LoggerFactory.getLogger(MyRedisCacheManager.class);

    /**
     * 缓存操作异常时处理方式
     * @return
     */
    @Override
    public CacheErrorHandler errorHandler() {
        return new CacheErrorHandler() {
            @Override
            public void handleCacheGetError(RuntimeException exception, Cache cache, Object key) {
                if (logger.isErrorEnabled()) {
                    logger.error(String.format("Failed to get data from cache[key=%s].", key), exception);
                }
            }

            @Override
            public void handleCachePutError(RuntimeException exception, Cache cache, Object key, Object value) {
                if (logger.isErrorEnabled()) {
                    logger.error(String.format("Failed to put cache data[key=%s, value=%s].", key, value), exception);
                }
            }

            @Override
            public void handleCacheEvictError(RuntimeException exception, Cache cache, Object key) {
                if (logger.isErrorEnabled()) {
                    logger.error(String.format("Failed to evict cache data[key=%s].", key), exception);
                }
            }

            @Override
            public void handleCacheClearError(RuntimeException exception, Cache cache) {
                if (logger.isErrorEnabled()) {
                    logger.error(String.format("Failed to clear cache data."), exception);
                }
            }
        };
    }

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig();
        config = config
                // 设置key的序列化方式为string
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                // 设置value的序列化方式为Jackson2JsonRedisSerializer
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(this.jackson2JsonRedisSerializer()))
                // 不缓存null值
                .disableCachingNullValues()
                // 设置缓存过期时间30分钟
                .entryTtl(Duration.ofMinutes(30L));

        // 特殊缓存空间应用不同的配置
        Map<String, RedisCacheConfiguration> map = new HashMap<>();
        map.put("bill_1", config.entryTtl(Duration.ofSeconds(30L))); // 设置bill_1缓存空间数据过期时间为30秒
        map.put("bill_1", config.entryTtl(Duration.ofHours(1L))); // 设置bill_2缓存空间数据过期时间为1小时

        // 使用自定义缓存配置初始化RedisCacheManager
        RedisCacheManager redisCacheManager = RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(config) // 默认配置
                .withInitialCacheConfigurations(map) // 特殊缓存配置
                .transactionAware() // 事务
                .build();
        return redisCacheManager;
    }

    /**
     * json序列化
     * @return
     */
    private RedisSerializer<Object> jackson2JsonRedisSerializer() {
        // 使用Jackson2JsonRedisSerializer序列化和反序列化Redis的value
        Jackson2JsonRedisSerializer<Object> serializer = new Jackson2JsonRedisSerializer<Object>(Object.class);

        // 解决查询缓存转换异常的问题
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);

        serializer.setObjectMapper(mapper);

        return serializer;
    }

}
