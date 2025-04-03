package gzg.momen.urlshortener.DTO;

import lombok.Data;

import java.time.Instant;

@Data
public class UrlStats {
    private String url;
    private String shortCode;
    private Instant createdAt;
    private Instant updatedAt;
    private long distinctClicks;
}
