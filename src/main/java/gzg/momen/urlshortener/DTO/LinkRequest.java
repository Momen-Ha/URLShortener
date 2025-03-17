package gzg.momen.urlshortener.DTO;

import lombok.Data;
import org.hibernate.validator.constraints.URL;

@Data
public class LinkRequest {

    @URL(message = "please enter correct url")
    private String url;
}
