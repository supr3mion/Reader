package com.nhlstenden.reader2.ComicParser;

import com.nhlstenden.reader2.models.Chapter;
import javafx.scene.image.Image;

import java.io.File;
import java.io.InputStream;
import java.util.*;
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

        // Map to store images and their file names
        Map<String, Image> imageMap = new HashMap<>();

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
                            imageMap.put(entry.getName(), image);
                        }
                    }
                }
            }


            // Sort the map entries by file name and add the images to the list
            imageMap.entrySet().stream()
                    .sorted((entry1, entry2) -> {
                        String name1 = entry1.getKey();
                        String name2 = entry2.getKey();
                        return compareFileNames(name1, name2);
                    })
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

    /**
     * Compares two file names, handling both numerical and alphabetical parts.
     *
     * @param name1 The first file name.
     * @param name2 The second file name.
     * @return A negative integer, zero, or a positive integer as the first argument is less than, equal to, or greater than the second.
     */
    private int compareFileNames(String name1, String name2) {
        String[] parts1 = name1.split("\\D+");
        String[] parts2 = name2.split("\\D+");

        int length = Math.min(parts1.length, parts2.length);
        for (int i = 0; i < length; i++) {
            if (parts1[i].isEmpty() || parts2[i].isEmpty()) {
                continue;
            }
            int num1 = Integer.parseInt(parts1[i]);
            int num2 = Integer.parseInt(parts2[i]);
            int result = Integer.compare(num1, num2);
            if (result != 0) {
                return result;
            }
        }
        return name1.compareTo(name2);
    }
}