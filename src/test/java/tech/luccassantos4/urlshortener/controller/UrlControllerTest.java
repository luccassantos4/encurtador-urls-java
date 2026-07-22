package tech.luccassantos4.urlshortener.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import tech.luccassantos4.urlshortener.service.UrlService;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class UrlControllerTest {

    private MockMvc mockMvc;
    private UrlService urlService;

    @BeforeEach
    void setUp() {
        urlService = mock(UrlService.class);
        UrlController controller = new UrlController(urlService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void shortenUrl_ShouldReturnShortenedUrl_WhenUrlIsValid() throws Exception {
        when(urlService.generateShortUrl(anyString())).thenReturn("abc123");

        mockMvc.perform(post("/shorten-url")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"url\": \"https://www.example.com\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.url").value("http://localhost/abc123"));
    }

    @Test
    void shortenUrl_ShouldReturnBadRequest_WhenUrlIsInvalid() throws Exception {
        when(urlService.generateShortUrl(anyString()))
                .thenThrow(new IllegalArgumentException("Invalid URL format: not-a-valid-url"));

        mockMvc.perform(post("/shorten-url")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"url\": \"not-a-valid-url\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void shortenUrl_ShouldReturnBadRequest_WhenUrlIsNull() throws Exception {
        when(urlService.generateShortUrl(any()))
                .thenThrow(new IllegalArgumentException("URL cannot be null or empty"));

        mockMvc.perform(post("/shorten-url")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"url\": null}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void redirectToOriginalUrl_ShouldReturn302_WhenIdExists() throws Exception {
        when(urlService.getOriginalUrl("abc123")).thenReturn("https://www.example.com");

        mockMvc.perform(get("/abc123"))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "https://www.example.com"));
    }

    @Test
    void redirectToOriginalUrl_ShouldReturn404_WhenIdDoesNotExist() throws Exception {
        when(urlService.getOriginalUrl("nonexistent")).thenReturn(null);

        mockMvc.perform(get("/nonexistent"))
                .andExpect(status().isNotFound());
    }
}
