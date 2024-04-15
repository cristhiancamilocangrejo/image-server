package com.bsl.image.configuration.service;

import com.bsl.image.configuration.exception.ImageConfigurationException;
import com.bsl.image.configuration.repository.ImageRepository;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ImageServiceTest {
    private ImageService imageService;

    @Mock
    private ImageRepository imageRepository;

    @BeforeEach
    public void setUp() {
        imageService = new ImageService(imageRepository);
    }

    @SneakyThrows
    @Test
    public void shouldGetPredefinedImageTypes() {
        when(imageRepository.getPredefinedImages()).thenReturn(List.of());

        var predefinedImages = imageService.getPredefinedImages();

        assertNotNull(predefinedImages);
        verify(imageRepository).getPredefinedImages();
    }

    @Test
    public void shouldFailGettingPredefinedImageTypes() throws IOException {
        when(imageRepository.getPredefinedImages()).thenThrow(new IOException("Error getting predefined images"));

        assertThrows(ImageConfigurationException.class, () -> imageService.getPredefinedImages());

        verify(imageRepository).getPredefinedImages();
    }
}
