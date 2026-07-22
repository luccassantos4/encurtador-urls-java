package tech.luccassantos4.urlshortener.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.time.LocalDateTime;

@Document(collection = "urls")
public class UrlEntity {
    @Id
    private String id;
    private String fullUrl;

    @Field(targetType = FieldType.DATE_TIME)
    @Indexed(expireAfterSeconds = 0)
    private LocalDateTime expiresAt;

    public UrlEntity(String id, String fullUrl, LocalDateTime expiresAt) {
        this.id = id;
        this.fullUrl = fullUrl;
        this.expiresAt = expiresAt;
    }

    public UrlEntity() {
    }

    public String getId() {
        return id;
    }

    public String getFullUrl() {
        return fullUrl;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }
}
