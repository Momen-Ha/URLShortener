package gzg.momen.urlshortener.service;


import gzg.momen.urlshortener.DTO.LinkRequest;
import gzg.momen.urlshortener.DTO.LinkResponse;
import gzg.momen.urlshortener.DTO.UrlMapper;
import gzg.momen.urlshortener.DTO.UrlStats;
import gzg.momen.urlshortener.exceptions.ShortCodeNotFoundException;
import gzg.momen.urlshortener.model.Url;
import gzg.momen.urlshortener.repository.UrlRepository;
import gzg.momen.urlshortener.utils.Base62Encoder;
import gzg.momen.urlshortener.utils.ZookeeperUtility;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.KeeperException;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Objects;


@Slf4j
@Service
public class UrlService implements IUrlService {

    private final UrlRepository urlRepository;
    private final UrlMapper urlMapper;
    private final ZookeeperUtility zookeeper;
    private final RedisService redisService;

    public UrlService(UrlRepository urlRepository, UrlMapper urlMapper, ZookeeperUtility zookeeper, RedisService redisService) {
        this.urlRepository = urlRepository;
        this.urlMapper = urlMapper;
        this.zookeeper = zookeeper;
        this.redisService = redisService;
    }

    @Override
    public LinkResponse createShortUrl(LinkRequest urlRequest) throws KeeperException.NoNodeException {
        Url url = new Url();
        String shortCode = generateShortCode();
        url.setShortCode(shortCode);
        url.setUrl(urlRequest.getUrl());
        url.setCreatedAt(Instant.now());
        urlRepository.save(url);

        redisService.addUrlToCache(urlMapper.toLinkResponse(url));
        redisService.addShortCodeToBloomFilter(shortCode);

        return urlMapper.toLinkResponse(url);
    }

    @Override
    public LinkResponse getFullUrl(String shortUrl, String ip) {
        if(!redisService.checkIfShortCodeDoesNotExistsInBloomFilter(shortUrl)) {
            throw new ShortCodeNotFoundException("Url with code "
                    + shortUrl + " not found");
        }

        LinkResponse cachedResponse = redisService.getUrlFromCache(shortUrl);

        redisService.addUserIpToHyperLogLog(shortUrl, ip);

        if(Objects.nonNull(cachedResponse)) {
            return cachedResponse;
        }

        Url url = getUrl(shortUrl);
        return urlMapper.toLinkResponse(url);
    }

    @Override
    @CachePut(value = "urls", key = "#shortUrl")
    public LinkResponse updateUrl(String shortUrl) throws KeeperException.NoNodeException {
        String shortCode = generateShortCode();
        Url url = getUrl(shortUrl);
        url.setShortCode(shortCode);
        url.setUpdatedAt(Instant.now());

        urlRepository.save(url);
        return urlMapper.toLinkResponse(url);
    }

    @Override
    @CacheEvict(value = "urls", key = "#shortUrl")
    public void deleteUrl(String shortUrl) {
        Url url = getUrl(shortUrl);
        urlRepository.delete(url);
    }


    private Url getUrl(String shortUrl) {
        Url url = urlRepository.findByShortCode(shortUrl);
        if (Objects.isNull(url)) {
            throw new ShortCodeNotFoundException("Url with code " + shortUrl + " not found");
        }
        return url;
    }

    private String generateShortCode() throws KeeperException.NoNodeException {
            Long nextCounter = zookeeper.getNextCount();
            return Base62Encoder.encode(nextCounter);
    }

    public UrlStats getUrlStatistics(String shortUrl) {
        Url url = getUrl(shortUrl);
        UrlStats urlStats = new UrlStats();
        urlStats.setUrl(url.getUrl());
        urlStats.setShortCode(url.getShortCode());
        urlStats.setCreatedAt(url.getCreatedAt());
        urlStats.setUpdatedAt(url.getUpdatedAt());
        long clicks = redisService.getUniqueCountForUrl(shortUrl);
        urlStats.setDistinctClicks(clicks);
        return urlStats;
    }
}
