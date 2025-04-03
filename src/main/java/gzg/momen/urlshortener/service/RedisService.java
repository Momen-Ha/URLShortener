package gzg.momen.urlshortener.service;

import gzg.momen.urlshortener.DTO.LinkResponse;
import gzg.momen.urlshortener.constants.RedisKeys;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.URL;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import redis.clients.jedis.JedisPooled;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import java.util.HashMap;
import java.util.Objects;

@Slf4j
@Service
public class RedisService {

    private final RedisTemplate<String, String> redisTemplate;
    private final JedisPooled jedisPooled;

    public RedisService(RedisTemplate<String, String> redisTemplate, JedisPooled jedisPooled) {
        this.redisTemplate = redisTemplate;
        this.jedisPooled = jedisPooled;
    }

    public void addUrlToCache(String shortCode, LinkResponse url) {
        HashMap<String, String> urlMap = new HashMap<>();
        urlMap.put("id", url.getId());
        urlMap.put("url", url.getUrl());
        urlMap.put("shortCode", url.getShortCode());
        urlMap.put("createdAt",
                Objects.nonNull(url.getCreatedAt()) ?
                        url.getCreatedAt().atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : "");

        urlMap.put("updatedAt",
                Objects.nonNull(url.getUpdatedAt()) ?
                        url.getUpdatedAt().atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : "");
        redisTemplate.opsForHash().putAll("link:" + shortCode, urlMap);
        log.info("Cached URL: {} -> {}", shortCode, url);
    }

    public void addUserIpToHyperLogLog(String shortCode, String ip) {
        String key = shortCode + RedisKeys.HYPERLOGLOG.getKey();
        redisTemplate.opsForHyperLogLog().add(key, ip);
        log.info("Added IP {} to HyperLogLog for {}", ip, shortCode);
    }

    public long getUniqueCountForUrl(String shortCode) {
        String key = shortCode + RedisKeys.HYPERLOGLOG.getKey();
        long count = redisTemplate.opsForHyperLogLog().size(key);
        log.info("Unique click count for {}: {}", shortCode, count);
        return count;
    }

    public void addShortCodeToBloomFilter(String shortCode) {
        jedisPooled.bfAdd(RedisKeys.BLOOMFILTER.getKey() ,shortCode);
        log.info("Added short code {} to BloomFilter", shortCode);
    }

    public boolean checkIfShortCodeExistsInBloomFilter(String shortCode) {
        return jedisPooled.bfExists(RedisKeys.BLOOMFILTER.getKey(),shortCode);
    }
}
