package gzg.momen.urlshortener.respository;

import gzg.momen.urlshortener.model.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UrlRepository extends JpaRepository<Url, Long> {
    Url findByShortCode(String shortCode);
//    Url findByUuid(UUID uuid);
}
