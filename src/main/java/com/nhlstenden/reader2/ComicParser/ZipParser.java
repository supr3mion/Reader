package com.nhlstenden.reader2.ComicParser;

import com.nhlstenden.reader2.models.Chapter;
import javafx.scene.image.Image;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ZipParser {

    /**
     * Parses a ZIP file to extract images and returns a Chapter.
     *
     * @param compressedComic The ZIP file to be parsed.
     * @param chapter The Chapter object to be updated.
     * @return Chapter containing the extracted images.
     * @throws Exception If an error occurs during parsing.
     */
    public Chapter parse(File compressedComic, Chapter chapter) throws Exception {

        // List to store the extracted images
        List<Image> images = new ArrayList<>();

        // Open the ZIP archive
        try (ZipFile zip = new ZipFile(compressedComic)) {

            // Iterate through each entry in the ZIP file
            Enumeration<? extends ZipEntry> entries = zip.entries();

            while (entries.hasMoreElements()) {

                // Get the next entry
                ZipEntry entry = entries.nextElement();

                if (!entry.isDirectory()) {

                    // Check if the entry is an image
                    if (isImageFile(entry.getName())) {

                        // If the entry is an image, create an Image object from the input stream
                        try (InputStream inputStream = zip.getInputStream(entry)) {
                            Image image = new Image(inputStream);
                            images.add(image);
                        }
                    }
                }
            }

            // Sort images by file name
            images.sort(Comparator.comparing(Image::getUrl));
            chapter.setPages(images);
        }

        // Return the chapter now with the pages set
        return chapter;
    }

    /**
     * Checks if a file is an image based on its extension.
     *
     * @param fileName The name of the file.
     * @return True if the file is an image, false otherwise.
     */
    private boolean isImageFile(String fileName) {
        String lowerCaseName = fileName.toLowerCase();
        return lowerCaseName.endsWith(".png") || lowerCaseName.endsWith(".jpg") || lowerCaseName.endsWith(".jpeg");
    }
}