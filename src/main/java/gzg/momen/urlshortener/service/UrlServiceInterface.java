package gzg.momen.urlshortener.service;

import gzg.momen.urlshortener.DTO.LinkRequest;
import gzg.momen.urlshortener.DTO.LinkResponse;

public interface UrlServiceInterface {
    LinkResponse createShortUrl(LinkRequest urlRequest);
    LinkResponse getFullUrl(String shortUrl);
    LinkResponse updateUrl(String shortUrl);
    void deleteUrl(String shortUrl);
}
