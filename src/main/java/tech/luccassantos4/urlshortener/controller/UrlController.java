package tech.luccassantos4.urlshortener.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import tech.luccassantos4.urlshortener.controller.dto.ShortenUrlRequest;
import tech.luccassantos4.urlshortener.controller.dto.ShortenUrlResponse;
import tech.luccassantos4.urlshortener.entities.UrlEntity;
import tech.luccassantos4.urlshortener.repository.UrlRepository;

import java.time.LocalDateTime;

@RestController
public class UrlController {

    private static final Logger log = LoggerFactory.getLogger(UrlController.class);

    private final UrlRepository urlRepository;

    public UrlController(UrlRepository urlRepository) {
        this.urlRepository = urlRepository;
    }

    @PostMapping(value = "/shorten-url")
    public ResponseEntity<ShortenUrlResponse> shortenUrl(@RequestBody ShortenUrlRequest request, HttpServletRequest httpServletRequest) {

        String id = null;
        boolean dbAccessible = true;

        try {
            do {
                id = RandomStringUtils.randomAlphanumeric(5, 10);
            } while (urlRepository.existsById(id));
        } catch (DataAccessException ex) {
            log.warn("MongoDB not accessible or unauthorized; skipping existence check", ex);
            dbAccessible = false;
            id = RandomStringUtils.randomAlphanumeric(8);
        }

        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(1);

        if (dbAccessible) {
            try {
                urlRepository.save(new UrlEntity(id, request.url(), expiresAt));
            } catch (DataAccessException ex) {
                log.warn("Failed to save URL to MongoDB", ex);
            }
        } else {
            log.info("Running without persisting due to DB auth issue");
        }

        var redirectUrl = httpServletRequest.getRequestURL().toString().replace("/shorten-url", "") + "/" + id;

        return ResponseEntity.ok(new ShortenUrlResponse(redirectUrl));
    }
}
