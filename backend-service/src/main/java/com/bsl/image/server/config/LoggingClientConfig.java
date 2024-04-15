package com.bsl.image.server.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoCredential;
import com.mongodb.client.MongoClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoClientFactoryBean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestOperations;

import java.util.Objects;


//@Profile("!local")
@Configuration
public class LoggingClientConfig {

    @Value("${logdb.endpoint}")
    private String loggingEndpoint;
    @Value("${logdb.name}")
    private String loggingDatabaseName;
    @Value("${logdb.username}")
    private String loggingUsername;
    @Value("${logdb.password}")
    private String loggingPassword;


    @Bean
    public MongoClientFactoryBean mongo() throws Exception {
        MongoClientFactoryBean mongo = new MongoClientFactoryBean();
        mongo.setHost(loggingEndpoint);
        mongo.setCredential(new MongoCredential[] { MongoCredential.createCredential(loggingUsername, loggingDatabaseName, loggingPassword.toCharArray()) } );
        return mongo;
    }

    @Bean
    public MongoTemplate mongoTemplate() throws Exception {
        return new MongoTemplate(Objects.requireNonNull(mongo().getObject()), loggingDatabaseName);
    }

}
