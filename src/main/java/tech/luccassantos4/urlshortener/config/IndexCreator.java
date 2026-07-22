package tech.luccassantos4.urlshortener.config;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.index.IndexOperations;
import org.springframework.stereotype.Component;

import java.time.Duration;

import tech.luccassantos4.urlshortener.entities.UrlEntity;

@Component
public class IndexCreator implements ApplicationRunner {
n    private final MongoTemplate mongoTemplate;
n    public IndexCreator(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }
    @Override
    public void run(ApplicationArguments args) {
        IndexOperations ops = mongoTemplate.indexOps(UrlEntity.class);
        Index ttlIndex = new Index().on("expiresAt", Sort.Direction.ASC).expire(Duration.ZERO);
        ops.ensureIndex(ttlIndex);
    }
}
