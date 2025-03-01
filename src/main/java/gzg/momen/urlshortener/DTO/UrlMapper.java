package gzg.momen.urlshortener.DTO;


import gzg.momen.urlshortener.model.Url;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UrlMapper {
    LinkResponse toLinkResponse(Url url);
    Url LinkResponseToUrl(LinkResponse linkResponse);
}
