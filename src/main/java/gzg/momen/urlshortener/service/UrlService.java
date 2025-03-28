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
import org.apache.zookeeper.KeeperException;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Objects;


@Service
public class UrlService implements IUrlService {

    private final UrlRepository urlRepository;
    private final UrlMapper urlMapper;
    private final ZookeeperUtility zookeeper;

    public UrlService(UrlRepository urlRepository, UrlMapper urlMapper, ZookeeperUtility zookeeper) {
        this.urlRepository = urlRepository;
        this.urlMapper = urlMapper;
        this.zookeeper = zookeeper;
    }

    @Override
    @Cacheable(value = "urls", key = "#urlRequest.url")
    public LinkResponse createShortUrl(LinkRequest urlRequest) throws KeeperException.NoNodeException {
        Url url = new Url();
        String shortCode = generateShortCode();
        url.setShortCode(shortCode);
        url.setUrl(urlRequest.getUrl());
        url.setCreatedAt(Instant.now());

        urlRepository.save(url);
        return urlMapper.toLinkResponse(url);
    }

    @Override
    @Cacheable(value = "urls", key = "#shortUrl")
    public LinkResponse getFullUrl(String shortUrl) {
        return urlMapper.toLinkResponse(getUrl(shortUrl));
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

    public UrlStats getUrlStatistics(String url) {
        return new UrlStats();
    }
}
