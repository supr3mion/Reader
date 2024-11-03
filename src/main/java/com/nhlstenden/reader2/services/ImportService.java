package com.nhlstenden.reader2.services;

import com.nhlstenden.reader2.ComicParser.Parser;
import com.nhlstenden.reader2.DataLibrary.ChapterLibrary;
import com.nhlstenden.reader2.DataLibrary.SerieLibrary;
import com.nhlstenden.reader2.controllers.DatabaseController;
import com.nhlstenden.reader2.models.Chapter;
import com.nhlstenden.reader2.models.Serie;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritablePixelFormat;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.IntBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Service class for importing comic series and chapters.
 */
public class ImportService {

    private final ChapterLibrary chapterLibrary = new ChapterLibrary();
    private final SerieLibrary serieLibrary = new SerieLibrary();
    private Serie selectedSerie = null;
    private Serie serieModel;
    private final List<String> supportedExtensions = List.of("*.cbz", "*.cbr", "*.zip", "*.nhlcomic");

    /**
     * Updates the Serie model with the given name, description, and list of comic files.
     *
     * @param serieName        the name of the series
     * @param serieDescription the description of the series
     * @param comicFiles       the list of comic files
     */
    public void updateSerieModel(String serieName, String serieDescription, List<File> comicFiles) {
        List<Chapter> chapters = new ArrayList<>();

        for (File file : comicFiles) {
            chapters.add(new Chapter(file.getName(), file, false, 0, null));
        }

        if (serieModel == null) {
            serieModel = new Serie(serieName, serieDescription, false, false, 0, false, "Unknown");
        } else {
            serieModel.setName(serieName);
            serieModel.setDescription(serieDescription);
        }

        serieModel.setChapters(chapters);
    }

    /**
     * Copies the given comic files to the target directory and updates the Serie model.
     *
     * @param serieName        the name of the series
     * @param serieDescription the description of the series
     * @param comicFiles       the list of comic files
     * @return true if the operation was successful, false otherwise
     */
    public boolean copyFiles(String serieName, String serieDescription, List<File> comicFiles) {
        // Get the URL of the target directory
        URL targetDirUrl = getClass().getResource("/com/nhlstenden/reader2/comics/" + serieName);
        // Get the resource path
        String resourcePath = getClass().getResource("/com/nhlstenden/reader2/").getPath();

        File targetDir = null;

        // If the target directory does not exist, create it
        if (targetDirUrl == null) {
            targetDir = new File(resourcePath + "comics/" + serieName);
            if (!targetDir.exists()) {
                targetDir.mkdirs();
            }
            targetDirUrl = getClass().getResource("/com/nhlstenden/reader2/comics/" + serieName);
            try {
                targetDir = Paths.get(targetDirUrl.toURI()).toFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                targetDir = Paths.get(targetDirUrl.toURI()).toFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Copy each comic file to the target directory
        for (File file : comicFiles) {
            try {
                Files.copy(file.toPath(), new File(targetDir, file.getName()).toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        boolean close = true;
        // Update the Serie model with the new name and description
        serieModel.setName(serieName);
        serieModel.setDescription(serieDescription);

        // If no Serie is selected, upload the new Serie model
        if (this.selectedSerie == null) {
            close = uploadSerieModel();
            File finalTargetDir = targetDir;
            new Thread(() -> saveCoverImage(finalTargetDir, comicFiles.getFirst())).start();
        } else {
            try {
                serieModel.setId(this.selectedSerie.getId());
            } catch (RuntimeException e) {
                throw new RuntimeException(e);
            }
        }

        // Upload the Chapter models
        uploadChapterModel();

        return close;
    }

    /**
     * Saves the cover image for the series.
     *
     * @param targetDir  the target directory where the cover image will be saved
     * @param comicFile the list of comic files from which the cover image will be generated
     */
    private void saveCoverImage(File targetDir, File comicFile) {
        try {
            // Generate the cover image from the first comic file
            Image coverImage = generateCoverImage(comicFile);

            // Get the width and height of the cover image
            int width = (int) coverImage.getWidth();
            int height = (int) coverImage.getHeight();

            // Create a BufferedImage to hold the cover image data
            BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

            // Get the PixelReader to read the pixels from the cover image
            PixelReader pixelReader = coverImage.getPixelReader();

            // Define the pixel format for reading the pixels
            WritablePixelFormat<IntBuffer> format = WritablePixelFormat.getIntArgbInstance();

            // Create a buffer to hold the pixel data
            int[] buffer = new int[width * height];

            // Read the pixels from the cover image into the buffer
            pixelReader.getPixels(0, 0, width, height, format, buffer, 0, width);

            // Set the pixel data in the BufferedImage
            bufferedImage.setRGB(0, 0, width, height, buffer, 0, width);

            // Create a file to save the cover image
            File coverFile = new File(targetDir, "cover.jpg");

            // Write the BufferedImage to the file in JPEG format
            ImageIO.write(bufferedImage, "jpg", coverFile);
        } catch (Exception e) {
            // Print the stack trace if an exception occurs
            e.printStackTrace();
        }
    }

    /**
     * Generates the cover image for the given comic file.
     *
     * @param file the comic file from which the cover image will be generated
     * @return the cover image as an Image object
     * @throws Exception if an error occurs during image generation
     */
    private Image generateCoverImage(File file) throws Exception {
        // Create a new Parser instance
        Parser parser = new Parser();

        // Set the compressed comic file in the parser
        parser.setCompressedComic(file);

        // Create a new Chapter instance for the cover
        Chapter coverChapter = new Chapter(file.getName(), file, false, 0, null);

        // Set the chapter in the parser
        parser.setChapter(coverChapter);

        // Parse the chapter to extract the cover image
        coverChapter = parser.parse();

        // Return the first page of the chapter as the cover image
        return coverChapter.getPage(0);
    }

    /**
     * Uploads the Serie model to the database.
     *
     * @return true if the upload was successful, false otherwise
     */
    private boolean uploadSerieModel() {
        // Get the instance of the DatabaseController
        DatabaseController dbController = DatabaseController.getInstance();

        // Check if a Serie with the same name already exists in the library
        if (serieLibrary.getSerieByName(serieModel.getName()) != null) {
            // Show an alert if a Serie with the same name already exists
            String alertMessage = "A Serie with the name:\n'" + serieModel.getName() + "'\nalready exists.\nPlease choose a different name.";
            Alert alert = new Alert(Alert.AlertType.INFORMATION, alertMessage, ButtonType.OK);
            alert.setTitle("Warning");
            alert.setHeaderText(null);
            alert.showAndWait();
            return false;
        }

        // Upload the Serie model to the database and get the generated ID
        int generatedId = dbController.uploadModel(serieModel);
        if (generatedId != -1) {
            // Set the generated ID to the Serie model
            serieModel.setId(generatedId);
            System.out.println("Serie model uploaded successfully with ID: " + generatedId);
        } else {
            // Show an alert if the upload failed
            System.err.println("Failed to upload Serie model.");
            String alertMessage = "Failed to save serie to database.";
            Alert alert = new Alert(Alert.AlertType.INFORMATION, alertMessage, ButtonType.OK);
            alert.setTitle("Warning");
            alert.setHeaderText(null);
            alert.showAndWait();
            return false;
        }
        return true;
    }

    /**
     * Uploads the Chapter models to the database.
     */
    private void uploadChapterModel() {
        // Get the instance of the DatabaseController
        DatabaseController dbController = DatabaseController.getInstance();

        // Iterate through each chapter in the Serie model
        for (Chapter chapter : serieModel.getChapters()) {
            // Skip the chapter if it already exists in the library
            if (chapterLibrary.getChapterByName(chapter.getTitle()) != null) {
                continue;
            }

            // Set the Serie ID for the chapter
            chapter.setSerieID(serieModel.getId());

            // Upload the chapter model to the database and get the generated ID
            int generatedId = dbController.uploadModel(chapter);

            if (generatedId != -1) {
                // Set the generated ID to the chapter model
                chapter.setId(generatedId);
                System.out.println("Chapter model uploaded successfully with ID: " + generatedId);
            } else {
                // Log an error message if the upload failed
                System.err.println("Failed to upload Chapter model.");
            }
        }
    }

    /**
     * Gets the current Serie model.
     *
     * @return the current Serie model
     */
    public Serie getSerieModel() {
        return this.serieModel;
    }
}