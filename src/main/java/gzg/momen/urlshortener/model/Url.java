package gzg.momen.urlshortener.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Generated;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "urls")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Url {
    @Id
    private String id;

    @NotBlank
    private String url;

    @NotBlank
    private String shortCode;

    @NotNull
    private Instant createdAt;
    private Instant updatedAt;

}
