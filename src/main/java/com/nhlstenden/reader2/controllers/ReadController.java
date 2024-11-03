package com.nhlstenden.reader2.controllers;

import com.nhlstenden.reader2.DataLibrary.ChapterLibrary;
import com.nhlstenden.reader2.models.Serie;
import com.nhlstenden.reader2.models.Chapter;
import com.nhlstenden.reader2.ComicParser.Parser;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.IllegalFormatCodePointException;

public class ReadController {

    private Stage primaryStage;

    @FXML
    private Button closeComicButton;

    @FXML
    private Button previousPageButton;

    @FXML
    private Button nextPageButton;

    @FXML
    private ImageView primaryImageView;

    @FXML
    private BorderPane mainBorderPane;

    @FXML
    private Text readerInformation;

    @FXML
    private ImageView imageViewRight;

    private Serie serie;
    private int currentPage = 0;
    private int currentChapter = 0;

    public void initialize() {
        // Initialize the controller, set up any necessary data or bindings
        closeComicButton.setOnAction(event -> handleCloseComicButtonAction());
        previousPageButton.setOnAction(event -> handlePreviousPageButtonAction());
        nextPageButton.setOnAction(event -> handleNextPageButtonAction());
    }

    /**
     * Cleans up resources used by the reader.
     *
     * This method clears the image references in the primary image view and
     * cancels any ongoing loading of pages in the chapters of the current series.
     * It also sets the pages of each chapter to null and suggests garbage collection.
     */
    public void cleanup() {
        // Clear image references
        primaryImageView.setImage(null);

        // Iterate through each chapter in the series
        for (Chapter chapter : serie.getChapters()) {
            if (chapter.getPages() != null) {
                // Cancel any ongoing loading for each page in the chapter
                for (Image page : chapter.getPages()) {
                    page.cancel();
                }
                // Set the pages of the chapter to null
                chapter.setPages(null);
            }
        }

        // Suggest garbage collection
        System.gc();
    }

    private void handleCloseComicButtonAction() {
        this.primaryStage.close();
    }

    /**
     * Handles the action for the previous page button.
     *
     * This method checks if the current page is the first page. If it is, it checks if there are previous chapters
     * and navigates to the last page of the previous chapter. If it is not the first page, it simply decrements the current page.
     * It then updates the primary image view with the new page or loads the chapter in the background if the pages are not available.
     */
    private void handlePreviousPageButtonAction() {
        if (currentPage == 0) {
            if (currentChapter > 0) {
                currentChapter--;

                // Disable navigation buttons while loading the previous chapter
                nextPageButton.setDisable(true);
                previousPageButton.setDisable(true);
            }
        } else {
            currentPage--;
        }

        if (serie.getChapters().get(currentChapter).getPages() != null) {
            // Update the primary image view with the previous page
            primaryImageView.setImage(serie.getChapters().get(currentChapter).getPages().get(currentPage));
            setReaderInfo(false);
        } else {
            // Set loading information and load the chapter in the background
            setReaderInfo(true);
            primaryImageView.setImage(null);
            loadChapterInBackground(currentChapter, false);
        }
    }

    /**
     * Handles the action for the next page button.
     *
     * This method checks if the current page is the last page of the current chapter.
     * If it is, it increments the current chapter and resets the current page to 0.
     * It then disables the navigation buttons while loading the next chapter.
     * If it is not the last page, it simply increments the current page.
     * It then updates the primary image view with the new page or loads the chapter in the background if the pages are not available.
     */
    private void handleNextPageButtonAction() {

        // Check if the current page is the last page of the current chapter
        if (currentPage == serie.getChapters().get(currentChapter).getPages().size() - 1) {
            // Move to the next chapter and reset the current page to 0
            currentChapter++;
            currentPage = 0;

            // Disable navigation buttons while loading the next chapter
            nextPageButton.setDisable(true);
            previousPageButton.setDisable(true);

        } else {
            // Move to the next page within the current chapter
            currentPage++;
        }

        // Check if the current chapter is the last chapter in the series
        if (currentChapter == serie.getChapters().size()) {
            // Close the primary stage if there are no more chapters
            this.primaryStage.close();
        } else if (serie.getChapters().get(currentChapter).getPages() != null) {
            // If the pages of the next chapter are already loaded, update the primary image view
            primaryImageView.setImage(serie.getChapters().get(currentChapter).getPages().get(currentPage));
            setReaderInfo(false);
        } else {
            // If the pages are not loaded, set loading information and load the chapter in the background
            setReaderInfo(true);
            primaryImageView.setImage(null);
            loadChapterInBackground(currentChapter, true);
        }

    }

    /**
     * Loads the specified chapter in the background.
     *
     * This method cancels any ongoing loading of pages in the chapters of the current series,
     * clears the pages of each chapter, and suggests garbage collection. It then starts a new thread
     * to parse and load the specified chapter. Once the chapter is loaded, it updates the primary image view
     * and the reader information, and re-enables the navigation buttons.
     *
     * @param chapterId The ID of the chapter to load.
     * @param nextPage  A boolean indicating whether to load the first page (true) or the last page (false) of the chapter.
     */
    private void loadChapterInBackground(int chapterId, boolean nextPage) {

        // Cancel any ongoing loading and clear the pages of each chapter
        for (Chapter chapter : serie.getChapters()) {
            if (chapter.getPages() != null) {
                for (Image page : chapter.getPages()) {
                    page.cancel(); // Cancel any ongoing loading
                }
                chapter.setPages(null);
            }
        }
        System.gc(); // Suggest garbage collection

        // Start a new thread to load the specified chapter
        Thread nextChapterThread = new Thread(() -> {
            Parser parser = new Parser();
            parser.setChapter(serie.getChapters().get(chapterId));
            File compressedFile = serie.getChapters().get(chapterId).getFile();
            parser.setCompressedComic(compressedFile);
            compressedFile = null;
            System.gc(); // Suggest garbage collection

            try {
                // Parse and load the chapter
                Chapter nextChapter = parser.parse();
                serie.getChapters().get(chapterId).setPages(nextChapter.getPages());

                nextChapter = null;
                System.gc(); // Suggest garbage collection

                if (true) {
                    // Update the UI on the JavaFX Application Thread
                    javafx.application.Platform.runLater(() -> {
                        ChapterLibrary chapterLibrary = new ChapterLibrary();

                        // Update the last read chapter
                        if (chapterLibrary.setLastRead(serie.getChapters().get(chapterId).getId())) {
                            System.out.println("Last read updated");
                        } else {
                            System.out.println("Failed to update last read");
                        }

                        // Update the primary image view with the first or last page of the chapter
                        if (nextPage) {
                            currentPage = 0;
                            primaryImageView.setImage(serie.getChapters().get(chapterId).getPages().getFirst());
                        } else {
                            currentPage = serie.getChapters().get(chapterId).getPages().size() - 1;
                            primaryImageView.setImage(serie.getChapters().get(chapterId).getPages().getLast());
                        }
                        setReaderInfo(false);
                        nextPageButton.setDisable(false);
                        previousPageButton.setDisable(false);
                    });
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            parser = null;
            System.gc(); // Suggest garbage collection
            Runtime.getRuntime().freeMemory();

        });

        nextChapterThread.start(); // Start the thread
    }

    /**
     * Sets the primary stage for this controller.
     *
     * This method is called from the parent controller to set the primary stage.
     * It also adds an event filter to the scene of the primary stage to handle key press events.
     * When the right arrow key is pressed, it triggers the action for the next page button if it is not disabled.
     * When the left arrow key is pressed, it triggers the action for the previous page button if it is not disabled.
     *
     * @param readStage The primary stage to be set for this controller.
     */
    public void setPrimaryStage(Stage readStage) {
        // Set the primary stage for this controller
        // This method is called from the parent controller
        this.primaryStage = readStage;

        // Add an event filter to handle key press events
        this.primaryStage.getScene().addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            // Check if the right arrow key is pressed
            if (event.getCode() == KeyCode.RIGHT) {
                // Trigger the action for the next page button if it is not disabled
                if (!nextPageButton.isDisabled()) {
                    handleNextPageButtonAction();
                    event.consume();
                }
            // Check if the left arrow key is pressed
            } else if (event.getCode() == KeyCode.LEFT) {
                // Trigger the action for the previous page button if it is not disabled
                if (!previousPageButton.isDisabled()) {
                    handlePreviousPageButtonAction();
                    event.consume();
                }
            }
        });
    }

    /**
     * Creates a binding for the height of the center area of the main border pane.
     *
     * This method returns an observable value that represents the height of the center area
     * of the main border pane, with a 40.0 pixel offset subtracted from the total height.
     *
     * @return An observable value representing the height of the center area.
     */
    private ObservableValue<? extends Number> centerScaleHeight() {
        return Bindings.createDoubleBinding(
                () -> mainBorderPane.getHeight() - 40.0,
                mainBorderPane.heightProperty()
        );
    }

    /**
     * Creates a binding for the width of the center area of the main border pane.
     *
     * This method returns an observable value that represents the width of the center area
     * of the main border pane, with an 80.0 pixel offset subtracted from the total width.
     *
     * @return An observable value representing the width of the center area.
     */
    private ObservableValue<? extends Number> centerScaleWidth() {
        return Bindings.createDoubleBinding(
                () -> mainBorderPane.getWidth() - 80.0,
                mainBorderPane.widthProperty()
        );
    }

    public void setCurrentChapter(int currentChapter) {
        this.currentChapter = currentChapter;
    }

    /**
     * Sets the series for this controller.
     *
     * This method initializes the series and updates the primary stage title to indicate loading.
     * It sets the reader information to loading, binds the primary image view's dimensions to the center area of the main border pane,
     * and sets the center of the main border pane to the primary image view. It then starts loading the first chapter in the background.
     *
     * @param serie The series to be set for this controller.
     */
    public void setSerie(Serie serie) {
        this.serie = serie;

        // Update the primary stage title to indicate loading
        this.primaryStage.setTitle(serie.getName() + " Loading...");
        setReaderInfo(true);

        // Set the application icon
        URL iconDir = getClass().getResource("/com/nhlstenden/reader2/images/icon.png");
        assert iconDir != null;
        this.primaryStage.getIcons().add(new Image(iconDir.toString()));

        // Bind the primary image view's dimensions to the center area of the main border pane
        primaryImageView.fitHeightProperty().bind(centerScaleHeight());
        primaryImageView.fitWidthProperty().bind(centerScaleWidth());
        mainBorderPane.setCenter(primaryImageView);

        // Set reader information to loading and clear the primary image view
        setReaderInfo(true);
        primaryImageView.setImage(null);

        // Load the first chapter in the background
        loadChapterInBackground(currentChapter, true);
    }

    /**
     * Sets the reader information text.
     *
     * This method updates the reader information text based on the loading status.
     * If loading is true, it sets the text to "Loading...". Otherwise, it sets the text
     * to display the current chapter and page information.
     *
     * @param loading A boolean indicating whether the reader is currently loading.
     */
    private void setReaderInfo(boolean loading) {
        // Set the reader information text

        if (loading) {
            readerInformation.setText("Loading...");
        } else {
            String chapterInfo = "Chapter " + (currentChapter + 1) + " / " + serie.getChapters().size();
            String pageInfo = "Page " + (currentPage + 1) + " / " + serie.getChapters().get(currentChapter).getPages().size();

            readerInformation.setText(chapterInfo + " - " + pageInfo);
        }
    }
}