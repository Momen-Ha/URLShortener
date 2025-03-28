package gzg.momen.urlshortener.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class RedisService {

    private final RedisTemplate<String, String> redisTemplate;

    public RedisService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void addUrlToCache(String shortCode, String url) {
        redisTemplate.opsForValue().set(shortCode, url);
        log.info("Cached URL: {} -> {}", shortCode, url);
    }

    public void addUserIpToHyperLogLog(String shortCode, String ip) {
        String key = shortCode + ":clicks";
        redisTemplate.opsForHyperLogLog().add(key, ip);
        log.info("Added IP {} to HyperLogLog for {}", ip, shortCode);
    }

    public long getUniqueCountForShortCode(String shortCode) {
        String key = shortCode + ":clicks";
        long count = redisTemplate.opsForHyperLogLog().size(key);
        log.info("Unique click count for {}: {}", shortCode, count);
        return count;
    }
}
