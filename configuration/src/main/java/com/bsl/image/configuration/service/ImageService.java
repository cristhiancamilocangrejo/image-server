package com.bsl.image.configuration.service;

import com.bsl.image.configuration.exception.ImageConfigurationException;
import com.bsl.image.configuration.repository.ImageRepository;
import com.bsl.image.server.commons.PredefinedImage;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
@AllArgsConstructor
@Log4j
public class ImageService {

    private final ImageRepository repository;

    public List<PredefinedImage> getPredefinedImages(){
        try {
            return repository.getPredefinedImages();
        } catch (IOException e) {
            log.error("Error occurred getting image predefined configuration", e);
            throw new ImageConfigurationException(e);
        }
    }
}
