package gzg.momen.urlshortener.service;

import gzg.momen.urlshortener.DTO.LinkRequest;
import gzg.momen.urlshortener.DTO.LinkResponse;
import org.apache.zookeeper.KeeperException;

public interface IUrlService {
    LinkResponse createShortUrl(LinkRequest urlRequest) throws KeeperException.NoNodeException;
    LinkResponse getFullUrl(String shortUrl);
    LinkResponse updateUrl(String shortUrl) throws KeeperException.NoNodeException;
    void deleteUrl(String shortUrl);
}
