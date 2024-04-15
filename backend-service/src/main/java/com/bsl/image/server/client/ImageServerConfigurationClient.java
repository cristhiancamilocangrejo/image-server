package com.bsl.image.server.client;

import com.bsl.image.server.commons.PredefinedImage;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
@AllArgsConstructor
public class ImageServerConfigurationClient {

    private final RestClient imageConfigurationClient;

    @Cacheable("predefinedImages")
    public List<PredefinedImage> getPredefinedImages(){
        return imageConfigurationClient.get().retrieve().body(new ParameterizedTypeReference<>() {});
    }
}
