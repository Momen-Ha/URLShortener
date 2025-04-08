package gzg.momen.urlshortener.service;

import gzg.momen.urlshortener.DTO.LinkResponse;
import gzg.momen.urlshortener.constants.RedisKeys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import redis.clients.jedis.JedisPooled;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import java.util.HashMap;
import java.util.Map;
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

    public void addUrlToCache(LinkResponse url) {
        HashMap<String, String> urlMap = new HashMap<>();
        urlMap.put("id", url.getId());
        urlMap.put("url", url.getUrl());
        urlMap.put("shortCode", url.getShortCode());
        urlMap.put("createdAt",
                Objects.nonNull(url.getCreatedAt()) ?
                        url.getCreatedAt().atZone(ZoneId.systemDefault())
                                .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : "");

        urlMap.put("updatedAt",
                Objects.nonNull(url.getUpdatedAt()) ?
                        url.getUpdatedAt().atZone(ZoneId.systemDefault())
                                .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : "");

        redisTemplate.opsForHash().putAll(RedisKeys.URL.getKey() +
                url.getShortCode(), urlMap);
        log.info("Cached URL: {} -> {}", url.getShortCode(), url);
    }

    public LinkResponse getUrlFromCache(String shortCode) {
        String redisKey = RedisKeys.URL.getKey() + shortCode;
        Map<Object, Object> urlMap = redisTemplate.opsForHash().entries(redisKey);

        if (Objects.isNull(urlMap) || urlMap.isEmpty()) {
            return null;
        }

        LinkResponse response = new LinkResponse();
        response.setId((String) urlMap.get("id"));
        response.setUrl((String) urlMap.get("url"));
        response.setShortCode((String) urlMap.get("shortCode"));
        if (urlMap.get("createdAt") != null && !((String) urlMap.get("createdAt")).isEmpty()) {
            response.setCreatedAt(
                    Instant.from(
                            DateTimeFormatter.ISO_LOCAL_DATE_TIME
                                    .withZone(ZoneId.systemDefault())
                                    .parse((String) urlMap.get("createdAt"))
                    )
            );
        }
        if (urlMap.get("updatedAt") != null && !((String) urlMap.get("updatedAt")).isEmpty()) {
            response.setUpdatedAt(
                    Instant.from(
                            DateTimeFormatter.ISO_LOCAL_DATE_TIME
                                    .withZone(ZoneId.systemDefault())
                                    .parse((String) urlMap.get("updatedAt"))
                    )
            );
        }

        return response;
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

    public boolean checkIfShortCodeDoesNotExistsInBloomFilter(String shortCode) {
        return jedisPooled.bfExists(RedisKeys.BLOOMFILTER.getKey(),shortCode);
    }
}
