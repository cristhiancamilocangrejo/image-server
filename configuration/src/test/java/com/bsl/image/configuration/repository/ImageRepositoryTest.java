package com.bsl.image.configuration.repository;

import com.bsl.image.configuration.exception.ImageConfigurationException;
import com.bsl.image.server.commons.PredefinedImage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.junit.jupiter.api.Assertions.*;

public class ImageRepositoryTest {

    private ImageRepository repository;

    @BeforeEach
    public void setUp() {
        repository = new ImageRepository();
        repository.setPredefinedImagesTypeFile("predefined_images.json");
    }

    @Test
    public void shouldGetPredefinedTypes() throws IOException {
        var predefinedImages = repository.getPredefinedImages();

        assertNotNull(predefinedImages);
        assertEquals(predefinedImages.size(), 2);

        var expectedTypes = predefinedImages.stream().map(PredefinedImage::name).collect(Collectors.toList());
        assertThat(expectedTypes, contains("thumbnail", "gif"));
    }

    @Test
    public void shouldFailGettingPredefinedTypesDueToBadFormatting() {
        repository.setPredefinedImagesTypeFile("predefined_images_malformed.json");

        assertThrows(IOException.class, () ->  repository.getPredefinedImages());

    }

}
