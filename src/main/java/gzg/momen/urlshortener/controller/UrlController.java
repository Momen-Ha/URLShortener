package gzg.momen.urlshortener.controller;


import gzg.momen.urlshortener.DTO.LinkRequest;
import gzg.momen.urlshortener.DTO.LinkResponse;
import gzg.momen.urlshortener.DTO.UrlStats;
import gzg.momen.urlshortener.service.ShortenerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/short")
public class ShortenerController {

    private final ShortenerService shortenerService;

    public ShortenerController(ShortenerService shortenerService) {
        this.shortenerService = shortenerService;
    }


    @PostMapping("/shorten")
    public ResponseEntity<LinkResponse> shorten(@RequestBody LinkRequest url) {
        LinkResponse createdUrl = shortenerService.createShortUrl(url);
        return new ResponseEntity<>(createdUrl, HttpStatus.CREATED);
    }


    @GetMapping("/{shortUrl}")
    public ResponseEntity<LinkResponse> getLink(@PathVariable String shortUrl) {
        LinkResponse fullUrl = shortenerService.getFullUrl(shortUrl);
        return new ResponseEntity<>(fullUrl, HttpStatus.TEMPORARY_REDIRECT);
    }

    @PutMapping("/shorten/{url}")
    public ResponseEntity<LinkResponse> updateLink(@PathVariable String url) {
        LinkResponse updatedUrl = shortenerService.updateUrl(url);
        return new ResponseEntity<>(updatedUrl, HttpStatus.OK);
    }

    @DeleteMapping("/shorten/{url}")
    public ResponseEntity<?> deleteLink(@PathVariable String url) {
        shortenerService.deleteUrl(url);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/shorten/{url}/stats")
    public ResponseEntity<UrlStats> getStats(@PathVariable String url) {
        UrlStats stats = shortenerService.getUrlStatistics(url);
        return new ResponseEntity<>(stats, HttpStatus.OK);
    }

}
