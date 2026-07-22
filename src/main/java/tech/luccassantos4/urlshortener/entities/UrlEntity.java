package tech.luccassantos4.urlshortener.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Date;

@Document(collection = "urls")
public class UrlEntity {
    @Id
    private String id;
    private String fullUrl;

    @Indexed(expireAfterSeconds = 0)
    private Date expiresAt;

    public UrlEntity(String id, String fullUrl, Date expiresAt) {
        this.id = id;
        this.fullUrl = fullUrl;
        this.expiresAt = expiresAt;
    }

    public UrlEntity() {
    }

    public String getId() {
        return id;
    }
}
