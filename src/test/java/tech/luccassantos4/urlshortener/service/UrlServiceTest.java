package tech.luccassantos4.urlshortener.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;
import tech.luccassantos4.urlshortener.entities.UrlEntity;
import tech.luccassantos4.urlshortener.repository.UrlRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UrlServiceTest {

    @Mock
    private UrlRepository urlRepository;

    private UrlService urlService;

    @BeforeEach
    void setUp() {
        urlService = new UrlService(urlRepository);
    }

    @Test
    void generateShortUrl_ShouldReturnId_WhenUrlIsValid() {
        String validUrl = "https://www.example.com";
        when(urlRepository.existsById(any())).thenReturn(false);
        when(urlRepository.save(any(UrlEntity.class))).thenReturn(new UrlEntity());

        String result = urlService.generateShortUrl(validUrl);

        assertNotNull(result);
        verify(urlRepository, atLeastOnce()).existsById(any());
        verify(urlRepository).save(any(UrlEntity.class));
    }

    @Test
    void generateShortUrl_ShouldThrowException_WhenUrlIsNull() {
        assertThrows(IllegalArgumentException.class, () -> urlService.generateShortUrl(null));
    }

    @Test
    void generateShortUrl_ShouldThrowException_WhenUrlIsEmpty() {
        assertThrows(IllegalArgumentException.class, () -> urlService.generateShortUrl(""));
    }

    @Test
    void generateShortUrl_ShouldThrowException_WhenUrlIsMalformed() {
        assertThrows(IllegalArgumentException.class, () -> urlService.generateShortUrl("not-a-valid-url"));
    }

    @Test
    void generateShortUrl_ShouldHandleDatabaseException_WhenMongoDBNotAccessible() {
        String validUrl = "https://www.example.com";
        when(urlRepository.existsById(any())).thenThrow(new DataAccessException("DB not accessible") {});

        String result = urlService.generateShortUrl(validUrl);

        assertNotNull(result);
        verify(urlRepository, atLeastOnce()).existsById(any());
    }

    @Test
    void getOriginalUrl_ShouldReturnUrl_WhenIdExists() {
        String id = "abc123";
        String expectedUrl = "https://www.example.com";
        UrlEntity entity = new UrlEntity(id, expectedUrl, LocalDateTime.now());
        
        when(urlRepository.findById(id)).thenReturn(Optional.of(entity));

        String result = urlService.getOriginalUrl(id);

        assertEquals(expectedUrl, result);
        verify(urlRepository).findById(id);
    }

    @Test
    void getOriginalUrl_ShouldReturnNull_WhenIdDoesNotExist() {
        String id = "nonexistent";
        
        when(urlRepository.findById(id)).thenReturn(Optional.empty());

        String result = urlService.getOriginalUrl(id);

        assertNull(result);
        verify(urlRepository).findById(id);
    }
}
