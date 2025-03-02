package gzg.momen.urlshortener.service;


import com.devskiller.friendly_id.FriendlyId;
import gzg.momen.urlshortener.DTO.LinkRequest;
import gzg.momen.urlshortener.DTO.LinkResponse;
import gzg.momen.urlshortener.DTO.UrlMapper;
import gzg.momen.urlshortener.DTO.UrlStats;
import gzg.momen.urlshortener.exceptions.shortCodeNotFoundException;
import gzg.momen.urlshortener.model.Url;
import gzg.momen.urlshortener.respository.UrlRepository;
import jakarta.transaction.Transactional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;


@Service
public class UrlService implements UrlServiceInterface {

    private final UrlRepository urlRepository;
    private final UrlMapper urlMapper;

    public UrlService(UrlRepository urlRepository, UrlMapper urlMapper) {
        this.urlRepository = urlRepository;
        this.urlMapper = urlMapper;
    }


    @Override
    @Transactional
    public LinkResponse createShortUrl(LinkRequest urlRequest) {

        UUID uuid;
        String shortCode = "";
        do {
            uuid = UUID.randomUUID();
            shortCode = FriendlyId.toFriendlyId(uuid);
        } while(Objects.nonNull(urlRepository.findByShortCode(shortCode)));

        Url url = new Url();
        url.setShortCode(shortCode);
        url.setId(uuid);
        url.setUrl(urlRequest.getUrl());
        url.setCreatedAt(Instant.now());

        urlRepository.save(url);

        return urlMapper.toLinkResponse(url);
    }

    @Override
    public LinkResponse getFullUrl(String shortUrl) {
        return urlMapper.toLinkResponse(getUrl(shortUrl));
    }

    @Override
    @Transactional
    public LinkResponse updateUrl(String shortUrl) {
        UUID uuid;
        String shortCode = "";
        do {
            uuid = UUID.randomUUID();
            shortCode = FriendlyId.toFriendlyId(uuid);
        } while(Objects.nonNull(urlRepository.findByShortCode(shortCode)));

        Url url = getUrl(shortUrl);
        url.setShortCode(shortCode);
        url.setUpdatedAt(Instant.now());
        urlRepository.save(url);
        return urlMapper.toLinkResponse(url);
    }

    @Override
    @Transactional
    public void deleteUrl(String shortUrl) {
        Url url = getUrl(shortUrl);
        urlRepository.delete(url);
    }

    public Url getUrl(String shortUrl) {
        Url url = urlRepository.findByShortCode(shortUrl);
        if (Objects.isNull(url)) {
            throw new shortCodeNotFoundException("Url with code " + shortUrl + " not found");
        }
        return url;
    }


    public UrlStats getUrlStatistics(String url) {
        return new UrlStats();
    }
}
