package com.bsl.image.server.controller;

import com.bsl.image.server.client.ImageServerConfigurationClient;
import com.bsl.image.server.commons.PredefinedImage;
import com.bsl.image.server.service.ImageDownloaderService;
import com.bsl.image.server.service.ImageService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import software.amazon.awssdk.http.HttpStatusCode;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping(value = "/v1/image")
@AllArgsConstructor
public class ImageController {

    private final ImageServerConfigurationClient imageServerConfigurationClient;

    private ImageDownloaderService imageDownloaderService;
    private final ImageService imageService;

    @GetMapping("/show/{predefined-type-name}/{seo-name}")
    public ResponseEntity<byte[]> getImage(@PathVariable("predefined-type-name") String predefinedTypeName,
                           @PathVariable(value = "seo-name", required = false) String seoName,
                           @RequestParam(name = "reference") String reference) throws IOException {

        var image = imageService.processImage(predefinedTypeName, seoName, reference);
        var headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=\"%s\"".formatted("test"));
        return ResponseEntity
                .status(HttpStatusCode.OK)
                .headers(headers)
                .contentType(MediaType.IMAGE_JPEG)
                .body(image);
    }

    @DeleteMapping("/flush/{predefined-type-name}")
    public ResponseEntity<Void> flushImage(@PathVariable("predefined-type-name") String predefinedTypeName,
                                     @RequestParam(name = "reference") String reference) {
        imageService.flushImage(predefinedTypeName, reference);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/predefinedImages")
    public ResponseEntity<List<PredefinedImage>> predefinedImages() {
        imageDownloaderService.downloadImage("person-sitting-at-a-desk-working.jpg");
        return ResponseEntity.ok(imageServerConfigurationClient.getPredefinedImages());
    }
}
