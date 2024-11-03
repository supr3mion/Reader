package com.nhlstenden.reader2.ComicParser;

import com.nhlstenden.reader2.models.Chapter;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.PixelReader;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class GifParser {

    /**
     * Parses a compressed comic file (ZIP) containing GIF images and extracts the frames from each GIF.
     *
     * @param compressedComic The ZIP file containing the compressed comic.
     * @param chapter The Chapter object to which the extracted images will be added.
     * @return The Chapter object with the extracted images set as pages.
     * @throws Exception If an error occurs during the parsing process.
     */
    public Chapter parse(File compressedComic, Chapter chapter) throws Exception {
        // List to store the extracted images
        List<Image> images = new ArrayList<>();

        // Open the ZIP archive
        try (ZipFile zip = new ZipFile(compressedComic)) {
            // Iterate through each entry in the ZIP file
            Iterator<? extends ZipEntry> entries = zip.entries().asIterator();

            while (entries.hasNext()) {
                // Get the next entry
                ZipEntry entry = entries.next();

                // Check if the entry is a GIF file
                if (!entry.isDirectory() && entry.getName().toLowerCase().endsWith(".gif")) {
                    // If the entry is a GIF file, process it
                    try (InputStream inputStream = zip.getInputStream(entry);
                         ImageInputStream imageStream = ImageIO.createImageInputStream(inputStream)) {
                        // Get an ImageReader for GIF format
                        Iterator<ImageReader> readers = ImageIO.getImageReadersByFormatName("gif");
                        if (!readers.hasNext()) {
                            throw new IllegalArgumentException("No GIF reader found");
                        }
                        ImageReader reader = readers.next();
                        reader.setInput(imageStream);

                        // Get the number of frames in the GIF
                        int frameCount = reader.getNumImages(true);
                        for (int i = 0; i < frameCount; i++) {
                            // Read each frame and convert it to a JavaFX Image
                            BufferedImage frame = reader.read(i);
                            Image image = convertToFxImage(frame);
                            images.add(image);
                        }
                    }
                }
            }
        }

        // Sort images by their index
        images.sort(Comparator.comparingInt(images::indexOf));
        chapter.setPages(images);

        return chapter;
    }

    /**
     * Converts a BufferedImage to a JavaFX Image.
     *
     * @param bufferedImage The BufferedImage to be converted.
     * @return The converted JavaFX Image.
     */
    private Image convertToFxImage(BufferedImage bufferedImage) {
        // Create a WritableImage with the same dimensions as the BufferedImage
        WritableImage writableImage = new WritableImage(bufferedImage.getWidth(), bufferedImage.getHeight());
        PixelWriter pixelWriter = writableImage.getPixelWriter();

        // Copy pixels from the BufferedImage to the WritableImage
        for (int y = 0; y < bufferedImage.getHeight(); y++) {
            for (int x = 0; x < bufferedImage.getWidth(); x++) {
                int argb = bufferedImage.getRGB(x, y);
                Color color = Color.rgb((argb >> 16) & 0xFF, (argb >> 8) & 0xFF, argb & 0xFF, ((argb >> 24) & 0xFF) / 255.0);
                pixelWriter.setColor(x, y, color);
            }
        }

        return writableImage;
    }
}