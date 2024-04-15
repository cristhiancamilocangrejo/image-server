package com.bsl.image.server.config;

import com.bsl.image.server.commons.PredefinedImage;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ImageConfigurationClientConfig {

    @Value("${image.server.configuration.client.url}")
    private String imageConfigurationServiceUrl;

    @Bean
    public RestClient imageConfigurationClient(RestTemplateBuilder restTemplateBuilder) {
        return RestClient.builder()
                .requestFactory(new HttpComponentsClientHttpRequestFactory())
                .messageConverters(converters -> converters.add(mappingJackson2HttpMessageConverter()))
                .baseUrl(imageConfigurationServiceUrl)
                .build();
    }

    @Bean
    public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
        var jsonConverter = new MappingJackson2HttpMessageConverter();
        var objectMapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.reader().forType(PredefinedImage.class);
        jsonConverter.setObjectMapper(objectMapper);
        return jsonConverter;
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}