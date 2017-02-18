package org.cloudfoundry.anycompany.poc.config.data;

import org.cloudfoundry.anycompany.poc.domain.Accessory;
import org.cloudfoundry.anycompany.poc.repositories.redis.RedisAccessoryRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@Profile("redis")
public class RedisConfig {

    @Bean
    public RedisAccessoryRepository redisRepository(RedisTemplate<String, Accessory> redisTemplate) {
        return new RedisAccessoryRepository(redisTemplate);
    }

    @Bean
    public RedisTemplate<String, Accessory> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Accessory> template = new RedisTemplate<>();

        template.setConnectionFactory(redisConnectionFactory);

        RedisSerializer<String> stringSerializer = new StringRedisSerializer();
        RedisSerializer<Accessory> calendarEventSerializer = new Jackson2JsonRedisSerializer<>(Accessory.class);

        template.setKeySerializer(stringSerializer);
        template.setValueSerializer(calendarEventSerializer);
        template.setHashKeySerializer(stringSerializer);
        template.setHashValueSerializer(calendarEventSerializer);

        return template;
    }

}
