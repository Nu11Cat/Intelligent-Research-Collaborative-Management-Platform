package cn.nullcat.sckj.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class TokenUtils {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String TOKEN_PREFIX = "token:";
    private static final Long TOKEN_EXPIRE = 604800000L; // 7天

    public void saveToken(String token, Integer userId) {
        String key = TOKEN_PREFIX + userId;
        redisTemplate.opsForValue().set(key, token, TOKEN_EXPIRE, TimeUnit.MILLISECONDS);
    }

    public boolean validateToken(String token, Integer userId) {
        String key = TOKEN_PREFIX + userId;
        Object savedToken = redisTemplate.opsForValue().get(key);
        return token.equals(savedToken);
    }

    public void removeToken(Integer userId) {

        String key = TOKEN_PREFIX + userId;
        redisTemplate.delete(key);
    }
}