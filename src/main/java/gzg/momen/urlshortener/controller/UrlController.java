package gzg.momen.urlshortener.controller;


import gzg.momen.urlshortener.DTO.LinkRequest;
import gzg.momen.urlshortener.DTO.LinkResponse;
import gzg.momen.urlshortener.DTO.UrlStats;
import gzg.momen.urlshortener.service.UrlService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/short")
public class UrlController {

    private final UrlService urlService;

    public UrlController(UrlService urlService) {
        this.urlService = urlService;
    }


    @PostMapping("/shorten")
    public ResponseEntity<LinkResponse> shorten(@RequestBody LinkRequest url) {
        LinkResponse createdUrl = urlService.createShortUrl(url);
        return new ResponseEntity<>(createdUrl, HttpStatus.CREATED);
    }


    @GetMapping("/{shortUrl}")
    public ResponseEntity<LinkResponse> getLink(@PathVariable String shortUrl) {
        LinkResponse fullUrl = urlService.getFullUrl(shortUrl);
        return new ResponseEntity<>(fullUrl, HttpStatus.TEMPORARY_REDIRECT);
    }

    @PutMapping("/shorten/{url}")
    public ResponseEntity<LinkResponse> updateLink(@PathVariable String url) {
        LinkResponse updatedUrl = urlService.updateUrl(url);
        return new ResponseEntity<>(updatedUrl, HttpStatus.OK);
    }

    @DeleteMapping("/shorten/{url}")
    public ResponseEntity<?> deleteLink(@PathVariable String url) {
        urlService.deleteUrl(url);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/shorten/{url}/stats")
    public ResponseEntity<UrlStats> getStats(@PathVariable String url) {
        UrlStats stats = urlService.getUrlStatistics(url);
        return new ResponseEntity<>(stats, HttpStatus.OK);
    }

}
