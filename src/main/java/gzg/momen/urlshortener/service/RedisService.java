package gzg.momen.urlshortener.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisService {

    private final RedisTemplate<String, String> redisTemplate;

    public RedisService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    
    public void addUserIpToHyperLogLog(String shortCode, String ip) {
        String key = shortCode + ":clicks" ;
        redisTemplate.opsForHyperLogLog().add(key, ip);
    }

    public long getUniqueCountForShortCode(String shortCode) {
        String key = shortCode + ":clicks" ;
        return redisTemplate.opsForHyperLogLog().size(key);
    }
}
