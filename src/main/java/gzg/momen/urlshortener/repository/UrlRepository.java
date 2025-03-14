package gzg.momen.urlshortener.repository;

import gzg.momen.urlshortener.model.Url;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UrlRepository extends MongoRepository<Url, String> {
    Url findByShortCode(String shortCode);
}
