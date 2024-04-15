package com.bsl.image.server.service;

import com.bsl.image.server.logging.LoggingService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ImageDownloaderService {

    private final LoggingService logging;

    private final RestTemplate restTemplate;

    @Value("${source.root.url}")
    private String sourceDomainUrl;

    @Value("${temp.folder.images:/temp}")
    private String tempFolderPath;


    public Optional<File> downloadImage(String imageReference) {
        try {
            var response = restTemplate.exchange(sourceDomainUrl + imageReference, HttpMethod.GET, null, byte[].class);
            if (response.getStatusCode() == HttpStatus.OK) {
                var imageBytes = response.getBody();
                if (imageBytes != null) {
                    var imageExtension = getImageExtension(imageReference);
                    var tempFileName = System.getProperty("user.dir") + tempFolderPath + "/" + RandomStringUtils.randomAlphanumeric(10) + new Date().getTime() + imageExtension;
                    try (var out = new FileOutputStream(tempFileName)) {
                        out.write(imageBytes);
                    }
                    return Optional.of(new File(tempFileName));
                }
            }
        } catch (IOException e) {
            logging.logError(String.format("Image reference %s could not be downloaded", imageReference), e);
        }
        return Optional.empty();
    }

    private static String getImageExtension(String imageUrl) {
        var extension = "";
        var lastDotIndex = imageUrl.lastIndexOf('.');
        if (lastDotIndex > 0) {
            extension = imageUrl.substring(lastDotIndex);
        }
        return extension;
    }
}
