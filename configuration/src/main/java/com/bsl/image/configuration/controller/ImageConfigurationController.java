package com.bsl.image.configuration.controller;

import com.bsl.image.configuration.service.ImageService;
import com.bsl.image.server.commons.PredefinedImage;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/image/configuration")
@AllArgsConstructor
public class ImageConfigurationController {

    private final ImageService service;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<PredefinedImage>> getPredefinedImages() {
        return ResponseEntity.ok(service.getPredefinedImages());
    }
}
