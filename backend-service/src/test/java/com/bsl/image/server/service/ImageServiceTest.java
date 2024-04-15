package com.bsl.image.server.service;

import com.bsl.image.server.client.ImageServerConfigurationClient;
import com.bsl.image.server.commons.ImageType;
import com.bsl.image.server.commons.PredefinedImage;
import com.bsl.image.server.commons.ScaleType;
import com.bsl.image.server.exception.ImageServiceException;
import com.bsl.image.server.logging.LoggingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ImageServiceTest {

    private static final String IMAGE_TEST = "person-sitting-at-a-desk-working.jpg";

    private ImageService imageService;

    @Mock
    private ImageServerConfigurationClient configurationClient;

    @Mock
    private ImageDownloaderService downloaderService;

    @Mock
    private S3Client s3Client;

    @Mock
    private LoggingService loggingService;

    @Mock
    private ImageOptimizator optimizator;

    @BeforeEach
    public void setUp() {
        when(configurationClient.getPredefinedImages()).thenReturn(getPredefinedImageTypes());
        this.imageService = new ImageService(configurationClient, downloaderService, s3Client, loggingService, optimizator);
    }

    @Test
    public void shouldFailDueToPredefinedImageNotPresent() {
        assertThrows(ImageServiceException.class, () -> imageService.processImage("thumbnail-gif", "dept-blazer", IMAGE_TEST));

        verify(configurationClient).getPredefinedImages();
    }

    @Test
    public void shouldReturnOptimizedImageSinceItExistsInS3() throws IOException {
        when(s3Client.headObject(any(HeadObjectRequest.class))).thenReturn(HeadObjectResponse.builder().build());
        when(s3Client.getObjectAsBytes(any(GetObjectRequest.class))).thenReturn(ResponseBytes.fromByteArray(GetObjectResponse.builder().build(), new byte[]{}));

        var imageByteArray = imageService.processImage("thumbnail", "dept-blazer", IMAGE_TEST);

        assertNotNull(imageByteArray);

        verify(configurationClient).getPredefinedImages();
        verify(s3Client).headObject(any(HeadObjectRequest.class));
        verify(s3Client).getObjectAsBytes(any(GetObjectRequest.class));
        verifyNoInteractions(loggingService);
    }

    @Test
    public void shouldReturnOptimizedImageWhenItDoesNotExistInS3ButOriginalDoes() throws IOException {
        when(s3Client.headObject(any(HeadObjectRequest.class))).thenThrow(NoSuchKeyException.class) // object not exists
                .thenReturn(HeadObjectResponse.builder().build()); //original does
        when(s3Client.getObjectAsBytes(any(GetObjectRequest.class))).thenReturn(ResponseBytes.fromByteArray(GetObjectResponse.builder().build(), new byte[]{}));
        when(optimizator.resizeAndOptimizeImage(any(PredefinedImage.class), any(File.class))).thenReturn(new File(Objects.requireNonNull(getClass().getClassLoader().getResource("images/person-sitting-at-a-desk-working.jpg")).getPath()));
        when(s3Client.putObject(any(PutObjectRequest.class), any(Path.class))).thenReturn(PutObjectResponse.builder().build());

        var imageByteArray = imageService.processImage("thumbnail", "dept-blazer", IMAGE_TEST);

        assertNotNull(imageByteArray);

        verify(configurationClient).getPredefinedImages();
        verify(s3Client, times(2)).headObject(any(HeadObjectRequest.class));
        verify(s3Client).getObjectAsBytes(any(GetObjectRequest.class));
        verify(s3Client).putObject(any(PutObjectRequest.class), any(Path.class));
        verifyNoInteractions(loggingService);
    }

    @Test
    public void shouldFailWhenTheImageDoesNotExistInSourceDomain() throws IOException {
        when(s3Client.headObject(any(HeadObjectRequest.class))).thenThrow(NoSuchKeyException.class) // object not exists
                .thenThrow(NoSuchKeyException.class); //original neither
        when(downloaderService.downloadImage(anyString())).thenReturn(Optional.empty());

        assertThrows(ImageServiceException.class, () -> imageService.processImage("thumbnail", "dept-blazer", IMAGE_TEST));

        verify(configurationClient).getPredefinedImages();
        verify(loggingService).logError(anyString(), any(ImageServiceException.class));
    }

    @Test
    public void shouldReturnOptimizedImageWhenItIsDownloadedFromSourceDomain() throws IOException {
        var imageFile = new File(Objects.requireNonNull(getClass().getClassLoader().getResource("images/person-sitting-at-a-desk-working.jpg")).getPath());

        when(s3Client.headObject(any(HeadObjectRequest.class))).thenThrow(NoSuchKeyException.class) // object not exists
                .thenThrow(NoSuchKeyException.class);
        when(downloaderService.downloadImage(anyString())).thenReturn(Optional.of(imageFile));
        when(optimizator.resizeAndOptimizeImage(any(PredefinedImage.class), any(File.class))).thenReturn(imageFile);
        when(s3Client.putObject(any(PutObjectRequest.class), any(Path.class))).thenReturn(PutObjectResponse.builder().build());

        var imageByteArray = imageService.processImage("thumbnail", "dept-blazer", IMAGE_TEST);

        assertNotNull(imageByteArray);

        verify(configurationClient).getPredefinedImages();
        verify(s3Client, times(2)).headObject(any(HeadObjectRequest.class));
        verify(s3Client, times(2)).putObject(any(PutObjectRequest.class), any(Path.class));
        verify(downloaderService).downloadImage(anyString());
        verifyNoInteractions(loggingService);
    }

    private List<PredefinedImage> getPredefinedImageTypes() {
        return List.of(new PredefinedImage("thumbnail", 500, 400, 90, ScaleType.FILL, null, ImageType.JPG, "future"),
                new PredefinedImage("gif", 300, 400, 60, ScaleType.CROP, null, ImageType.PNG, null));
    }
}
