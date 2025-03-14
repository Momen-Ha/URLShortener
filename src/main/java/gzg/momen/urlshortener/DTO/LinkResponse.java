package gzg.momen.urlshortener.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.time.Instant;

@Data
@AllArgsConstructor
public class LinkResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    private String url;
    private String shortCode;
    private Instant createdAt;
    private Instant updatedAt;
}
