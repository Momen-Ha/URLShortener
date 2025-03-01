package gzg.momen.urlshortener.DTO;

import java.time.Instant;

public record LinkResponse(
        Long id,
        String url,
        String shortCode,
        Instant createdAt,
        Instant updatedAt

) {

}
