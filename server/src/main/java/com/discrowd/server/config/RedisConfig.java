package com.discrowd.server.config;

import com.discrowd.server.model.status.UserStatusUpdate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    public static final String TOPIC_USER_STATUS = "user_status_updates";

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());

        Jackson2JsonRedisSerializer<Object> generalJacksonSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
        template.setValueSerializer(generalJacksonSerializer);

        Jackson2JsonRedisSerializer<UserStatusUpdate> userStatusJacksonSerializer = new Jackson2JsonRedisSerializer<>(UserStatusUpdate.class);
        template.setHashValueSerializer(userStatusJacksonSerializer);

        template.afterPropertiesSet();
        return template;
    }
}
