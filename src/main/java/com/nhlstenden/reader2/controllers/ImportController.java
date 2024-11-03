package com.nhlstenden.reader2.controllers;

import com.nhlstenden.reader2.models.Serie;
import com.nhlstenden.reader2.services.ImportService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ImportController {

    private Stage primaryStage;
    private final ImportService importService = new ImportService();

    @FXML
    private Button cancelComicImportButton;

    @FXML
    private Button selectComicsButton;

    @FXML
    private ListView<String> comicsListView;

    @FXML
    private TextArea serieDescription;

    @FXML
    private TextArea serieTags;

    @FXML
    private TextField serieName;

    @FXML
    private Button emptyListButton;

    @FXML
    private Button copySelectedComics;

    private List<File> comicFiles = new ArrayList<>();
    private Serie selectedSerie;

    /**
     * Initializes the ImportController.
     *
     * This method sets up the event handlers for the buttons in the import view.
     * It assigns actions to the selectComicsButton, cancelComicImportButton, emptyListButton, and copySelectedComics button.
     * The actions include selecting files, closing the primary stage, clearing the comics list and series name, and copying files.
     */
    public void initialize() {
        // Set the action for the selectComicsButton to open a file chooser dialog
        selectComicsButton.setOnAction(event -> selectFiles());

        // Set the action for the cancelComicImportButton to close the primary stage
        cancelComicImportButton.setOnAction(event -> primaryStage.close());

        // Set the action for the emptyListButton to clear the comics list view and series name
        emptyListButton.setOnAction(event -> {
            comicsListView.getItems().clear();
            serieName.setText("");
            comicFiles.clear();
        });

        // Set the action for the copySelectedComics button to copy the selected files
        copySelectedComics.setOnAction(event -> copyFiles());
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    /**
     * Opens a file chooser dialog to select comic files and updates the series model.
     *
     * This method opens a file chooser dialog to allow the user to select multiple comic files.
     * It filters the files to only show comic file types (cbz, cbr, zip, nhlcomic).
     * If files are selected, it performs the following actions:
     * - If the series name is empty, it sets the series name to the name of the first selected file (without extension).
     * - Adds the names of the selected files to the comics list view.
     * - Adds the selected files to the comicFiles list.
     * Finally, it updates the series model with the selected files.
     */
    private void selectFiles() {
        // Create a file chooser dialog
        FileChooser fileChooser = new FileChooser();
        // Add filters to only show comic file types
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Comic Files", "*.cbz", "*.cbr", "*.zip", "*.nhlcomic"));
        // Show the file chooser dialog and get the selected files
        List<File> selectedFiles = fileChooser.showOpenMultipleDialog(primaryStage);

        // If files are selected
        if (selectedFiles != null) {
            for (File file : selectedFiles) {
                // If the series name is empty, set it to the name of the first selected file (without extension)
                if (serieName.getText().isEmpty()) {
                    serieName.setText(file.getName().split("\\.")[0]);
                }
                // Add the name of the selected file to the comics list view
                comicsListView.getItems().add(file.getName());
                // Add the selected file to the comicFiles list
                comicFiles.add(file);
            }
        }

        // Update the series model with the selected files
        importService.updateSerieModel(serieName.getText(), serieDescription.getText(), comicFiles);
    }

    /**
     * Copies the selected comic files to the destination and closes the primary stage if successful.
     *
     * This method calls the `copyFiles` method of the `importService` to copy the selected comic files.
     * If the copy operation is successful (indicated by the `close` boolean), it performs the following actions:
     * - Clears the items in the comics list view.
     * - Clears the comicFiles list.
     * - Closes the primary stage.
     */
    private void copyFiles() {
        boolean close = importService.copyFiles(serieName.getText(), serieDescription.getText(), comicFiles);
        if (close) {
            comicsListView.getItems().clear();
            comicFiles.clear();
            primaryStage.close();
        }
    }

   /**
     * Sets the selected series and updates the UI components accordingly.
     *
     * This method sets the selected series to the provided `serie` object.
     * It disables the `serieTags`, `serieName`, and `serieDescription` UI components.
     * It also updates the `serieName` and `serieDescription` fields with the name and description of the selected series.
     *
     * @param serie the `Serie` object to be set as the selected series
     */
    public void setSelectedSerie(Serie serie) {
        this.selectedSerie = serie;
        serieTags.setDisable(true);
        serieName.setDisable(true);
        serieDescription.setDisable(true);
        serieName.setText(this.selectedSerie.getName());
        serieDescription.setText(this.selectedSerie.getDescription());
    }

    /**
     * Cleans the UI components and clears the comic files list.
     *
     * This method performs the following actions:
     * - Clears the items in the comics list view.
     * - Resets the text of the series name, description, and tags fields to empty strings.
     * - Clears the comicFiles list.
     */
    public void clean() {
        comicsListView.getItems().clear();
        serieName.setText("");
        serieDescription.setText("");
        serieTags.setText("");
        comicFiles.clear();
    }
}