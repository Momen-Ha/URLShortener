package gzg.momen.urlshortener.controller;


import gzg.momen.urlshortener.DTO.LinkRequest;
import gzg.momen.urlshortener.DTO.LinkResponse;
import gzg.momen.urlshortener.DTO.UrlStats;
import gzg.momen.urlshortener.service.UrlService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.apache.zookeeper.KeeperException;
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
    public ResponseEntity<LinkResponse> shorten(@RequestBody @Valid LinkRequest linkRequest) throws KeeperException.NoNodeException {
        LinkResponse createdUrl = urlService.createShortUrl(linkRequest);
        return new ResponseEntity<>(createdUrl, HttpStatus.CREATED); 
    }


    @GetMapping("/{shortUrl}")
    public ResponseEntity<LinkResponse> getLink(@PathVariable String shortUrl,
                                                HttpServletRequest httpServletRequest) {
        String ip = httpServletRequest.getRemoteAddr();
        LinkResponse fullUrl = urlService.getFullUrl(shortUrl, ip);
        return new ResponseEntity<>(fullUrl, HttpStatus.TEMPORARY_REDIRECT);
    }

    @PutMapping("/shorten/{shortUrl}")
    public ResponseEntity<LinkResponse> updateLink(@PathVariable String shortUrl) throws KeeperException.NoNodeException {
        LinkResponse updatedUrl = urlService.updateUrl(shortUrl);
        return new ResponseEntity<>(updatedUrl, HttpStatus.OK);
    }

    @DeleteMapping("/shorten/{shortUrl}")
    public ResponseEntity<?> deleteLink(@PathVariable String shortUrl) {
        urlService.deleteUrl(shortUrl);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/shorten/{shortUrl}/stats")
    public ResponseEntity<UrlStats> getStats(@PathVariable String shortUrl) {
        UrlStats stats = urlService.getUrlStatistics(shortUrl);
        return new ResponseEntity<>(stats, HttpStatus.OK);
    }

}
