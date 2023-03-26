/*
package com.user.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.net.UnknownHostException;

@Configuration
public class redisConfig {
    @Bean
    public RedisTemplate<String,Object> redisTemplate(RedisConnectionFactory connectionFactory)
            throws UnknownHostException {
//        创建 RedisTemplate对象
        RedisTemplate<String, Object> stringObjectRedisTemplate = new RedisTemplate<>();
//        设置连接工厂
        stringObjectRedisTemplate.setConnectionFactory(connectionFactory);
//        创建Json序列化工具
        GenericJackson2JsonRedisSerializer genericJackson2JsonRedisSerializer = new GenericJackson2JsonRedisSerializer();
//        设置key的序列化
        stringObjectRedisTemplate.setKeySerializer(RedisSerializer.string());
        stringObjectRedisTemplate.setHashKeySerializer(RedisSerializer.string());
//        设置value序列化
        stringObjectRedisTemplate.setValueSerializer(genericJackson2JsonRedisSerializer);
        stringObjectRedisTemplate.setHashKeySerializer(genericJackson2JsonRedisSerializer);
        return stringObjectRedisTemplate;

    }
}
*/
