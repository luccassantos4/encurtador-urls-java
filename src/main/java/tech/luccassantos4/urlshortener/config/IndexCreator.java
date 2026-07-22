package tech.luccassantos4.urlshortener.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.index.IndexOperations;
import org.springframework.stereotype.Component;

import java.time.Duration;
nimport tech.luccassantos4.urlshortener.entities.UrlEntity;

@Component
public class IndexCreator implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(IndexCreator.class);
    private final MongoTemplate mongoTemplate;

    public IndexCreator(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void run(ApplicationArguments args) {
        try {
            IndexOperations ops = mongoTemplate.indexOps(UrlEntity.class);
            Index ttlIndex = new Index().on("expiresAt", Sort.Direction.ASC).expire(Duration.ZERO);
            String name = ops.ensureIndex(ttlIndex);
            log.info("Ensured TTL index: {}", name);
        } catch (Exception e) {
            log.warn("Could not create TTL index (permission issue?): {}", e.getMessage());
        }
    }
}
