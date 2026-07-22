package tech.luccassantos4.urlshortener.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.luccassantos4.urlshortener.controller.dto.ShortenUrlRequest;
import tech.luccassantos4.urlshortener.controller.dto.ShortenUrlResponse;
import tech.luccassantos4.urlshortener.service.UrlService;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@RestController
public class UrlController {

    private final UrlService urlService;

    public UrlController(UrlService urlService) {
        this.urlService = urlService;
    }

    @PostMapping(value = "/shorten-url")
    public ResponseEntity<ShortenUrlResponse> shortenUrl(@RequestBody ShortenUrlRequest request, HttpServletRequest httpServletRequest) {
        String id = urlService.generateShortUrl(request.url());

        var redirectUrl = httpServletRequest.getRequestURL().toString().replace("/shorten-url", "") + "/" + id;

        return ResponseEntity.ok(new ShortenUrlResponse(redirectUrl));
    }

    @GetMapping("{id}")
    public ResponseEntity<String> redirectToOriginalUrl(@PathVariable String id) {
        String originalUrl = urlService.getOriginalUrl(id);

        if (originalUrl == null) {
            return ResponseEntity.notFound().build();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(originalUrl));

        return ResponseEntity.status(HttpStatus.FOUND).headers(headers).build();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(IllegalArgumentException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());
        return ResponseEntity.badRequest().body(error);
    }
}
