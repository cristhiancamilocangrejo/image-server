package com.bsl.image.server.service;

import com.bsl.image.server.client.ImageServerConfigurationClient;
import com.bsl.image.server.commons.PredefinedImage;
import com.bsl.image.server.exception.ImageServiceException;
import com.bsl.image.server.logging.LoggingService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ImageService {

    private static final String ORIGINAL_NAMESPACE = "original";

    private final ImageServerConfigurationClient configurationClient;

    private final ImageDownloaderService downloaderService;

    private final S3Client s3Client;

    private final LoggingService loggingService;

    private final ImageOptimizator imageOptimizator;

    @Value("${image.bucket.name}")
    private String bucketName;

    @Value("${temp.folder.images:/temp}")
    private String tempFolderPath;

    public void flushImage(String predefinedTypeImage, String reference) {
        getPredefinedTypeImage(predefinedTypeImage);
        var pathImage = getImageReferencePath(predefinedTypeImage, reference);
        s3Client.deleteObject(DeleteObjectRequest.builder().bucket(bucketName).key(pathImage).build());
    }

    public byte[] processImage(String predefinedTypeImage, String seoName, String reference) throws IOException {
        var predefinedImage = getPredefinedTypeImage(predefinedTypeImage);
        var path = getImageReferencePath(predefinedTypeImage, reference);
        if (checkIfOptimizedImageExists(path)) {
            var storedImage = getImage(path);
            if (storedImage.isEmpty()) {
                loggingService.logInfo(String.format("Error trying to get optimized image: %s", path));
                throw new ImageServiceException();
            }
            return Files.readAllBytes(storedImage.get().toPath());
        } else {
            var image = !checkIfOriginalImageExists(reference) ? downloadOriginalImage(reference) : getOriginalImage(reference);
            if (image.isEmpty()) {
                loggingService.logError("Original image not found", new ImageServiceException());
                throw new ImageServiceException();
            }

            var optimizedImage = resizeOptimizeImage(predefinedImage, image.get());
            storeImage(path, optimizedImage);
            return Files.readAllBytes(optimizedImage.toPath());
        }
    }

    private PredefinedImage getPredefinedTypeImage(String predefinedTypeImage) {
        var predefinedImageOpt = this.getPredefinedImage(predefinedTypeImage);
        if (predefinedImageOpt.isEmpty()) {
            loggingService.logInfo(String.format("The requested predefined image type: %s does not exist.", predefinedTypeImage));
            throw new ImageServiceException();
        }
        return predefinedImageOpt.get();
    }

    private Optional<File> getOriginalImage(String reference) {
        var pathImage = getImageReferencePath(ORIGINAL_NAMESPACE, reference);
        return getImage(pathImage);
    }

    private Optional<File> getImage(String pathImage) {
        var response = s3Client.getObjectAsBytes(GetObjectRequest.builder().bucket(bucketName).key(pathImage).build());
        var filePath = System.getProperty("user.dir") + tempFolderPath + "/" + pathImage;
        var fileImage = new File(filePath);
        if (!fileImage.getParentFile().exists())
            fileImage.getParentFile().mkdirs();
        if (!fileImage.exists()) {
            try (var fos = new FileOutputStream(filePath)) {
                fos.write(response.asByteArray());
                return Optional.of(fileImage);
            } catch (IOException e) {
                loggingService.logError("Error getting image from S3", e);
            }
        } else {
            return Optional.of(fileImage);
        }
        return Optional.empty();
    }

    private void storeImage(String path, File image) {
        s3Client.putObject(PutObjectRequest.builder().bucket(bucketName).key(path).build(), image.toPath());
    }

    private File resizeOptimizeImage(PredefinedImage predefinedImage, File image) {
        try {
            return imageOptimizator.resizeAndOptimizeImage(predefinedImage, image);
        } catch (IOException e) {
            loggingService.logError("Error trying to resize/optimize image", e);
            throw new ImageServiceException();
        }
    }

    private Optional<File> downloadOriginalImage(String reference) {
        var downloadedImage = downloaderService.downloadImage(reference);
        if (downloadedImage.isPresent()) {
            var path = getImageReferencePath(ORIGINAL_NAMESPACE, reference);
            var fileImage = downloadedImage.get();
            storeImage(path, fileImage);
            return Optional.of(fileImage);
        }
        return Optional.empty();
    }

    private boolean checkIfOriginalImageExists(String reference) {
        var pathImage = getImageReferencePath(ORIGINAL_NAMESPACE, reference);
        return checkIfObjectExists(pathImage);
    }

    private boolean checkIfOptimizedImageExists(String pathImage) {
        return checkIfObjectExists(pathImage);
    }

    private boolean checkIfObjectExists(String pathImage) {
        try {
            s3Client.headObject(HeadObjectRequest.builder().bucket(bucketName).key(pathImage).build());
        } catch (Exception ex) {
            return false;
        }
        return true;
    }

    private String getImageReferencePath(String predefinedImage, String reference) {
        var imageFileName = reference.substring(0, reference.lastIndexOf('.'));
        var path = new StringBuilder(predefinedImage).append("/");
        if (imageFileName.length() > 4) {
            path.append(imageFileName, 0, 4).append("/");
        }
        if (imageFileName.length() > 8) {
            path.append(imageFileName, 4, 8).append("/");
        }
        path.append(reference);
        return path.toString();
    }

    public Optional<PredefinedImage> getPredefinedImage(String predefinedTypeImage) {
        return configurationClient.getPredefinedImages().stream()
                .filter(image -> image.name().equalsIgnoreCase(predefinedTypeImage)).findFirst();
    }
}
