package gzg.momen.urlshortener.respository;

import gzg.momen.urlshortener.model.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UrlRespository extends JpaRepository<Url, Long> {
    Url findByShortUrl(String shortUrl);
}
