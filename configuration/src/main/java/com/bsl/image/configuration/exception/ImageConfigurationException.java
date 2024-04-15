package com.bsl.image.configuration.exception;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.IOException;

@AllArgsConstructor
@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class ImageConfigurationException extends RuntimeException {

    public ImageConfigurationException(IOException e) {
        super(e);
    }
}
