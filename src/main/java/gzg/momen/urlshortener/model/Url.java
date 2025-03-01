package gzg.momen.urlshortener.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Url {
    @Id
    @Column(nullable = false)
    @NotNull
    private UUID id;

    @Column(nullable = false)
    @NotBlank
    private String url;

    @Column(nullable = false)
    @NotBlank
    private String shortCode;

    @Column(nullable = false)
    @NotNull
    private Instant createdAt;
    private Instant updatedAt;

}
