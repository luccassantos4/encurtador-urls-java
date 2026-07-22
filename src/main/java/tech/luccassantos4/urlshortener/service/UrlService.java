package tech.luccassantos4.urlshortener.service;

import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import tech.luccassantos4.urlshortener.entities.UrlEntity;
import tech.luccassantos4.urlshortener.repository.UrlRepository;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;

@Service
public class UrlService {

    private static final Logger log = LoggerFactory.getLogger(UrlService.class);
    private static final int EXPIRATION_MINUTES = 1;

    private final UrlRepository urlRepository;

    public UrlService(UrlRepository urlRepository) {
        this.urlRepository = urlRepository;
    }

    public String generateShortUrl(String originalUrl) {
        validateUrl(originalUrl);
        
        String id = generateUniqueId();
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(EXPIRATION_MINUTES);

        saveUrlEntity(id, originalUrl, expiresAt);

        return id;
    }

    public String getOriginalUrl(String id) {
        var url = urlRepository.findById(id);
        if (url.isEmpty()) {
            return null;
        }
        return url.get().getFullUrl();
    }

    private String generateUniqueId() {
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

        return id;
    }

    private void saveUrlEntity(String id, String originalUrl, LocalDateTime expiresAt) {
        boolean dbAccessible = true;

        try {
            urlRepository.existsById(id);
        } catch (DataAccessException ex) {
            log.warn("MongoDB not accessible; skipping save", ex);
            dbAccessible = false;
        }

        if (dbAccessible) {
            try {
                urlRepository.save(new UrlEntity(id, originalUrl, expiresAt));
            } catch (DataAccessException ex) {
                log.warn("Failed to save URL to MongoDB", ex);
            }
        } else {
            log.info("Running without persisting due to DB auth issue");
        }
    }

    private void validateUrl(String url) {
        if (url == null || url.trim().isEmpty()) {
            throw new IllegalArgumentException("URL cannot be null or empty");
        }

        try {
            new URL(url);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Invalid URL format: " + url, e);
        }
    }
}
