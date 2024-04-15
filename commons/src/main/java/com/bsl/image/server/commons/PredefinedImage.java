package com.bsl.image.server.commons;

public record PredefinedImage(String name, int height, int width, int quality, ScaleType scaleType, String fillColor,
                              ImageType imageType, String sourceName) {

}
