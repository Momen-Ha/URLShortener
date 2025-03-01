package gzg.momen.urlshortener.service;


import com.devskiller.friendly_id.FriendlyId;
import gzg.momen.urlshortener.DTO.LinkRequest;
import gzg.momen.urlshortener.DTO.LinkResponse;
import gzg.momen.urlshortener.DTO.UrlMapper;
import gzg.momen.urlshortener.DTO.UrlStats;
import gzg.momen.urlshortener.model.Url;
import gzg.momen.urlshortener.respository.UrlRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;


@Service
public class ShortenerService {

    private final UrlRepository urlRepository;
    private final UrlMapper urlMapper;

    public ShortenerService(UrlRepository urlRepository, UrlMapper urlMapper) {
        this.urlRepository = urlRepository;
        this.urlMapper = urlMapper;
    }

    public Url getUrl(String shortUrl) {
        return urlRepository.findByShortCode(shortUrl);
    }



    @Transactional
    public LinkResponse createShortUrl(LinkRequest urlRequest) {

        UUID uuid;
        String shortCode = "";
        do {
            uuid = UUID.randomUUID();
            shortCode = FriendlyId.toFriendlyId(uuid);
        } while(Objects.nonNull(urlRepository.findByShortCode(shortCode)));


//        if(urlRepository.findByShortCode(shortCode) != null) {
//
//        }
        Url url = new Url();
        url.setShortCode(shortCode);
        url.setId(uuid);
        url.setUrl(urlRequest.getUrl());
        url.setCreatedAt(Instant.now());

        urlRepository.save(url);

        return urlMapper.toLinkResponse(url);
    }

    public LinkResponse getFullUrl(String shortUrl) {
        return urlMapper.toLinkResponse(getUrl(shortUrl));
    }

    public LinkResponse updateUrl(String link) {
        Url url = getUrl(link);
        url.setUrl(link);
        url.setUpdatedAt(Instant.now());
        urlRepository.save(url);
        return urlMapper.toLinkResponse(url);
    }

    public void deleteUrl(String shortUrl) {
        Url url = getUrl(shortUrl);
        urlRepository.delete(url);
    }

    public UrlStats getUrlStatistics(String url) {
        return new UrlStats();
    }
}
