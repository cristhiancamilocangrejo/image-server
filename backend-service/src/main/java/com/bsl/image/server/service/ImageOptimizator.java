package com.bsl.image.server.service;

import com.bsl.image.server.commons.PredefinedImage;
import org.springframework.stereotype.Service;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import java.io.File;
import java.io.IOException;

@Service
public class ImageOptimizator {

    public static final String ORIGINAL = "original";

    public File resizeAndOptimizeImage(PredefinedImage predefinedImage, File inputFile) throws IOException {
        var inputImage = ImageIO.read(inputFile);

        var writers = ImageIO.getImageWritersByFormatName(getImageExtension(inputFile.getName()));
        var writer = writers.next();

        var outputFile = new File(inputFile.getPath().replace(ORIGINAL, predefinedImage.name()));
        if (!outputFile.getParentFile().exists())
            outputFile.getParentFile().mkdirs();

        var outputStream = ImageIO.createImageOutputStream(outputFile);
        writer.setOutput(outputStream);

        var params = writer.getDefaultWriteParam();
        params.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        params.setCompressionQuality((float) predefinedImage.quality() / 100);
        writer.write(null, new IIOImage(inputImage, null, null), params);

        outputStream.close();
        writer.dispose();

        return outputFile;
    }

    private String getImageExtension(String imageUrl) {
        var extension = "";
        var lastDotIndex = imageUrl.lastIndexOf('.');
        if (lastDotIndex > 0) {
            extension = imageUrl.substring(lastDotIndex + 1);
        }
        return extension;
    }
}
