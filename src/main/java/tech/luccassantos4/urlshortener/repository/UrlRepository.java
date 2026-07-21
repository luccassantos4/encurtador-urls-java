package tech.luccassantos4.urlshortener.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import tech.luccassantos4.urlshortener.entities.UrlEntity;

public interface UrlRepository extends MongoRepository<UrlEntity, String> {
}
