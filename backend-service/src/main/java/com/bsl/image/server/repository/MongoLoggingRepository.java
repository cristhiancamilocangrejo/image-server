package com.bsl.image.server.repository;

import com.bsl.image.server.entity.Logging;
import lombok.AllArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
public class MongoLoggingRepository implements LoggingRepository {

    private static final String MONGO_DEFAULT_COLLECTION = "logs";
    private final MongoTemplate mongoTemplate;

    @Override
    public void loggingMessage(Logging logging) {
        mongoTemplate.insert(logging, MONGO_DEFAULT_COLLECTION);
    }

}
