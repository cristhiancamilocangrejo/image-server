package com.bsl.image.configuration.repository;

import com.bsl.image.server.commons.PredefinedImage;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.List;

@Repository
@Setter
public class ImageRepository {

    @Value("${predefined.images.type.file}")
    private String predefinedImagesTypeFile;

    public List<PredefinedImage> getPredefinedImages() throws IOException {
        var objectMapper = new ObjectMapper();
        return objectMapper.readValue(getClass().getClassLoader().getResourceAsStream(predefinedImagesTypeFile),
                new TypeReference<>() {});

    }
}
