package com.nhlstenden.reader2.ComicParser;

import com.github.junrar.Archive;
import com.github.junrar.rarfile.FileHeader;
import com.nhlstenden.reader2.models.Chapter;
import javafx.scene.image.Image;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class RarParser {

    /**
     * Parses a RAR file to extract images and returns a Chapter.
     *
     * @param compressedComic The RAR file to be parsed.
     * @param chapter The Chapter object to be updated.
     * @return Chapter containing the extracted images.
     * @throws Exception If an error occurs during parsing.
     */
    public Chapter parse(File compressedComic, Chapter chapter) throws Exception {

        // List to store the extracted images
        List<Image> images = new ArrayList<>();

        // Map to store images and their file names
        Map<String, Image> imageMap = new HashMap<>();

        // Open the RAR archive
        try (Archive archive = new Archive(compressedComic)) {

            // FileHeader to store the current file in the archive
            FileHeader fileHeader;

            // Iterate through each file in the archive
            while ((fileHeader = archive.nextFileHeader()) != null) {

                // Check if the file is a directory
                if (!fileHeader.isDirectory()) {

                    // Check if the file is an image
                    if (isImageFile(fileHeader.getFileNameString())) {

                        // If the file is an image, create an Image object from the input stream
                        try (InputStream inputStream = archive.getInputStream(fileHeader)) {

                            // Create an Image object from the input stream
                            Image image = new Image(inputStream);

                            // Add the image and its file name to the map
                            imageMap.put(fileHeader.getFileNameString(), image);
                        }
                    }
                }
            }

            // Sort the map entries by file name and add the images to the list
            imageMap.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .forEachOrdered(entry -> images.add(entry.getValue()));

            // Set the sorted images to the chapter
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