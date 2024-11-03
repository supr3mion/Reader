package com.nhlstenden.reader2.controllers;

import com.nhlstenden.reader2.DataLibrary.ChapterLibrary;
import com.nhlstenden.reader2.DataLibrary.SerieLibrary;
import com.nhlstenden.reader2.models.Chapter;
import com.nhlstenden.reader2.models.Serie;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.stage.Modality;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.time.LocalDateTime;

import org.apache.commons.io.FileUtils;

public class MainController {


    @FXML
    private Button addSeries;

    @FXML
    TextField serieSearch;

    @FXML
    ListView<String> seriesListView;

    @FXML
    private ImageView serieCoverImage;

    @FXML
    private Text serieTitle;

    @FXML
    private Text serieDescription;

    @FXML
    private ListView<String> chaptersListView;

    @FXML
    private Button deleteSerieButton;

    @FXML
    private Button addChapterButton;

    @FXML
    private Button deleteChapterButton;

    @FXML
    private Button startReadingButton;

    @FXML
    private Button continueReadingButton;

    @FXML
    private Button readSelectedChapterButton;

    private Stage stage;

    SerieLibrary serieLibrary = new SerieLibrary();
    ChapterLibrary chapterLibrary = new ChapterLibrary();

    private Serie selectedSerie;
    private Chapter selectedChapter;

    /**
     * Initializes the MainController.
     * Sets up event handlers for buttons and loads the series.
     * Adds a listener to the series list view to display the selected serie.
     * Adds a listener to the search field to filter the series list view.
     */
    public void initialize() {

        // Set the action for the addSeries button to open the comic import view with no selected serie
        addSeries.setOnAction(event -> openComicImport(null));

        // Load the series into the series list view
        loadSeries();

        // Add a listener to the series list view to display the selected serie when the selection changes
        seriesListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> displaySerie(newValue));

        // Set the action for the deleteSerieButton to delete the selected serie
        deleteSerieButton.setOnAction(event -> deleteSerie());

        // Set the action for the addChapterButton to open the comic import view with the selected serie
        addChapterButton.setOnAction(event -> openComicImport(selectedSerie));

        // Set the action for the deleteChapterButton to delete the selected chapter
        deleteChapterButton.setOnAction(event -> deleteChapter());

        // Add a listener to the search field to filter the series list view
        serieSearch.textProperty().addListener((observable, oldValue, newValue) -> filterSeriesList(newValue));

        startReadingButton.setOnAction(event -> openReader(0));

        continueReadingButton.setOnAction(event -> openReader(null));

        readSelectedChapterButton.setOnAction(event -> openReader(chaptersListView.getSelectionModel().getSelectedIndex()));

        // Print an empty line to the console (for debugging purposes)
        System.out.println();

        chaptersListView.getItems().clear();
        serieTitle.setText("");
        serieDescription.setText("");
        serieCoverImage.setImage(null);
    }

    /**
     * Filters the series list view based on the search query.
     *
     * @param query The search query entered by the user.
     */
    void filterSeriesList(String query) {
        ObservableList<String> filteredList = FXCollections.observableArrayList();

        for (Serie serie : serieLibrary.getAllSeries()) {
            if (serie.getName().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(serie.getName());
            }
        }

        seriesListView.setItems(filteredList);

        filteredList = null;
        System.gc();
    }

    /**
     * Loads the series from the serieLibrary and populates the seriesListView with their names.
     * Creates an observable list of series names and sets it to the seriesListView.
     */
    private void loadSeries() {
        // Create an observable list to hold the names of the series
        ObservableList<String> seriesNames = FXCollections.observableArrayList();

        // Iterate through all the series in the serieLibrary
        for (Serie serie : serieLibrary.getAllSeries()) {
            // Add the name of each series to the observable list
            seriesNames.add(serie.getName());
        }

        // Set the observable list as the items of the seriesListView
        seriesListView.setItems(seriesNames);

        seriesNames = null;
        System.gc();
    }

    /**
     * Displays the details of the selected serie.
     *
     * This method performs the following steps:
     * 1. Retrieves the serie by its name from the serieLibrary.
     * 2. Sets the selectedSerie to the retrieved serie.
     * 3. If the serie is null, the method returns immediately.
     * 4. Updates the UI components with the serie's details:
     *    - Sets the serie's name to the serieTitle text field.
     *    - Sets the serie's description to the serieDescription text field.
     *    - Retrieves and sets the chapters of the serie.
     *    - Retrieves and sets the cover image of the serie.
     *    - Clears and populates the chaptersListView with the titles of the chapters.
     *
     * @param serieName the name of the serie to be displayed
     */
    private void displaySerie(String serieName) {
        // Retrieve the serie by its name from the serieLibrary
        Serie serie = serieLibrary.getSerieByName(serieName);
        selectedSerie = serie;

        // If the serie is null, return immediately
        if (serie == null) {
            return;
        }

        // Update the UI components with the serie's details
        serieTitle.setText(serie.getName());
        serieDescription.setText(serie.getDescription());

        // Retrieve and set the chapters of the serie
        serie.setChapters(chapterLibrary.getChapterBySerie(serie.getId()));

        for (Chapter chapter : serie.getChapters()) {

            URL resourceUrl = getClass().getResource("/com/nhlstenden/reader2/comics/" + serieName + "/" + chapter.getTitle());
            if (resourceUrl != null) {

                File file = null;

                try {
                    file = Paths.get(resourceUrl.toURI()).toFile();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                chapter.setFile(file);

                file = null;
                System.gc();

            }
        }

        // Retrieve and set the cover image of the serie
        Image coverImage = getCoverImage(serie.getName());
        serieCoverImage.setImage(coverImage);

        coverImage = null;

        // Clear and populate the chaptersListView with the titles of the chapters
        chaptersListView.getItems().clear();
        for (Chapter chapter : serie.getChapters()) {
            chaptersListView.getItems().add(chapter.getTitle());
        }
    }

    /**
     * Deletes the selected chapter from the chapter library and the file system.
     *
     * @throws Exception If an error occurs during file deletion.
     */
    private void deleteChapter() {
        // Retrieve the selected chapter title from the chaptersListView
        String chapterTitle = chaptersListView.getSelectionModel().getSelectedItem();

        // Retrieve the chapter object by its title from the chapterLibrary
        Chapter chapter = chapterLibrary.getChapterByName(chapterTitle);

        // Delete the chapter from the chapterLibrary using its ID
        chapterLibrary.deleteChapter(chapter.getId());

        chapter = null;

        // Remove the chapter title from the chaptersListView
        chaptersListView.getItems().remove(chapterTitle);

        // Retrieve the name of the current serie from the serieTitle text field
        String serieName = serieTitle.getText();

        try {
            // Attempt to delete the chapter file from the file system
            URL resourceUrl = getClass().getResource("/com/nhlstenden/reader2/comics/" + serieName + "/" + chapterTitle);
            if (resourceUrl != null) {
                File file = Paths.get(resourceUrl.toURI()).toFile();
                if (file.delete()) {
                    // Print a success message if the file is deleted successfully
                    System.out.println("File deleted successfully");
                } else {
                    // Print a failure message if the file deletion fails
                    System.out.println("Failed to delete the file");
                }
            }
        } catch (Exception e) {
            // Catch and print any exceptions that occur during file deletion
            e.printStackTrace();
        };

        serieName = null;
        chapterTitle = null;
        System.gc();
    }

    /**
     * Deletes the selected serie from the serie library and the file system.
     */
    public void deleteSerie() {
        // Retrieve the name of the selected serie from the serieTitle text field
        String serieName = serieTitle.getText();

        // Retrieve the serie object by its name from the serieLibrary
        Serie serie = serieLibrary.getSerieByName(serieName);

        // Delete the serie from the serieLibrary using its ID
        serieLibrary.deleteSerie(serie.getId());

        // Delete all chapters associated with the serie from the chapterLibrary
        chapterLibrary.deleteChapterBySerie(serie.getId());

        // Clear the UI components related to the serie
        chaptersListView.getItems().clear();
        serieTitle.setText("");
        serieDescription.setText("");
        serieCoverImage.setImage(null);

        try {
            // Attempt to delete the serie directory from the file system
            URL resourceUrl = getClass().getResource("/com/nhlstenden/reader2/comics/" + serieName);
            if (resourceUrl != null) {
                File file = Paths.get(resourceUrl.toURI()).toFile();
                FileUtils.deleteDirectory(file);
                file.delete();
            }
        } catch (Exception e) {
            // Catch and print any exceptions that occur during directory deletion
            e.printStackTrace();
        }

        // Reload the series list view
        loadSeries();

        serieName = null;
        serie = null;
        System.gc();
    }

    /**
     * Retrieves the cover image for a given serie.
     *
     * This method attempts to load the cover image from the file system based on the serie title.
     * If the cover image is found, it is converted to a JavaFX Image and returned.
     * If any exception occurs during this process, the exception is caught and printed, and null is returned.
     *
     * @param serieTitle The title of the serie for which the cover image is to be retrieved.
     * @return The cover image as a JavaFX Image, or null if the image is not found or an error occurs.
     */
    private Image getCoverImage(String serieTitle) {
        try {
            // Construct the URL to the cover image file based on the serie title
            URL resourceUrl = getClass().getResource("/com/nhlstenden/reader2/comics/" + serieTitle + "/cover.jpg");
            if (resourceUrl != null) {
                // Convert the URL to a File object
                File imageFile = Paths.get(resourceUrl.toURI()).toFile();
                // Convert the BufferedImage to a JavaFX Image and return it
                return SwingFXUtils.toFXImage(ImageIO.read(imageFile), null);
            }
        } catch (Exception e) {
            // Catch and print any exceptions that occur during the image loading process
            e.printStackTrace();
        }

        System.gc();

        // Return null if the image is not found or an error occurs
        return null;
    }

    /**
     * Opens the comic import view.
     *
     * This method loads the `import-view.fxml` file and creates a new stage for the comic import view.
     * It sets the stage properties, including the title and modality. If a series is selected, it sets
     * the selected series in the import controller. When the import stage is closed, it reloads the series
     * and cleans up the import controller.
     *
     * @param selectedSerie The series to be set in the import controller, or null if no series is selected.
     */
    public void openComicImport(Serie selectedSerie) {
        try {
            // Load the `import-view.fxml` file
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/nhlstenden/reader2/views/import-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load());

            // Create a new stage for the new view
            Stage importStage = new Stage();
            importStage.setScene(scene);
            importStage.setTitle("Comics Import");
            importStage.setResizable(false);

            // Set the modality to block the current stage
            importStage.initModality(Modality.APPLICATION_MODAL);
            importStage.initOwner(stage);

            // Get the controller for the import view and set the primary stage
            ImportController importController = fxmlLoader.getController();
            importController.setPrimaryStage(importStage);

            // If a series is selected, set it in the import controller
            if (selectedSerie != null) {
                importController.setSelectedSerie(selectedSerie);
            }

            // When the import stage is closed, reload the series and clean up the import controller
            importStage.setOnHiding(event -> {
                loadSeries();
                importController.clean();
            });

            // Show the import stage and wait for it to close
            importStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Opens the comic reader view.
     *
     * This method loads the `reader-view.fxml` file and creates a new stage for the comic reader view.
     * It sets the stage properties, including the title, modality, and minimum dimensions.
     * The method sets the current chapter in the reader controller based on the provided chapter index
     * or the last read chapter if the index is null. It hides the main stage and shows the reader stage.
     * When the reader stage is closed, it reloads the chapters, updates the UI, and shows the main stage again.
     *
     * @param currentChapter The index of the chapter to be opened, or null to open the last read chapter.
     */
    public void openReader(Integer currentChapter) {
        try {
            // Load the `reader-view.fxml` file
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/nhlstenden/reader2/views/reader-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load());

            // Create a new stage for the reader view
            Stage readStage = new Stage();
            readStage.setScene(scene);
            readStage.setTitle("Comic Reader");
            readStage.initModality(Modality.APPLICATION_MODAL);
            readStage.initOwner(stage);
            readStage.setMinHeight(460);
            readStage.setMinWidth(620);

            // Get the controller for the reader view and set the primary stage
            ReadController readController = fxmlLoader.getController();
            readController.setPrimaryStage(readStage);

            // Set the current chapter in the reader controller
            if (currentChapter != null) {
                readController.setCurrentChapter(currentChapter);
            } else {
                int lastReadChapter = findLastReadChapter();
                readController.setCurrentChapter(lastReadChapter);
            }

            // Set the selected series in the reader controller
            readController.setSerie(selectedSerie);

            // Hide the main stage
            this.stage = (Stage) addSeries.getScene().getWindow();
            this.stage.hide();

            // When the reader stage is closed, reload the chapters, update the UI, and show the main stage
            readStage.setOnHiding(event -> {
                selectedSerie.setChapters(chapterLibrary.getChapterBySerie(selectedSerie.getId()));
                displaySerie(selectedSerie.getName());
                readController.cleanup();
                this.stage.show();
            });

            // Show the reader stage and wait for it to close
            readStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Finds the index of the last read chapter.
     *
     * This method iterates through the chapters of the selected series and finds the chapter
     * with the most recent last read date. It returns the index of this chapter.
     *
     * @return The index of the last read chapter.
     */
    private int findLastReadChapter() {
        int lastReadChapterIndex = 0;
        LocalDateTime latestDate = null;

        // Iterate through the chapters of the selected series
        for (int i = 0; i < selectedSerie.getChapters().size(); i++) {
            Chapter chapter = selectedSerie.getChapters().get(i);
            LocalDateTime lastRead = chapter.getLastRead();

            // Check if the chapter has been read and if it is the most recently read chapter
            if (lastRead != null && (latestDate == null || lastRead.isAfter(latestDate))) {
                latestDate = lastRead;
                lastReadChapterIndex = i;
            }
        }

        return lastReadChapterIndex;
    }

}