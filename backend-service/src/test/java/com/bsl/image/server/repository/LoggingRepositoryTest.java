package com.bsl.image.server.repository;

import com.bsl.image.server.entity.Logging;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataMongoTest
@ActiveProfiles("dev")
@Import(LoggingRepositoryTest.MongoConfigurationTest.class)
public class LoggingRepositoryTest {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private MongoLoggingRepository mongoLoggingRepository;

    @Test
    public void insertLogging() {
       mongoLoggingRepository.loggingMessage(new Logging("test", null));

       var list = mongoTemplate.query(Logging.class).all();
       assertNotNull(list);
    }


    @TestConfiguration
    static class MongoConfigurationTest {

        @Autowired
        private MongoTemplate mongoTemplate;

        @Bean
        public MongoLoggingRepository mongoLoggingRepository() {
            return new MongoLoggingRepository(mongoTemplate);
        }
    }


}
