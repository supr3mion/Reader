package com.nhlstenden.reader2;

import com.nhlstenden.reader2.controllers.DatabaseController;
import com.nhlstenden.reader2.controllers.ReadController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class Main extends Application {

    // start the program
    @Override
    public void start(Stage stage) throws IOException {

        // Test the database connection
        DatabaseController dbController = DatabaseController.getInstance();
        if (dbController.executeQuery("SELECT 1") != null) {
            System.out.println("Database connection successful.");
        } else {
            System.err.println("Database connection failed.");
        }

        // load the main-view.fxml file
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("views/main-view.fxml"));

        // set the controller
        Scene scene = new Scene(fxmlLoader.load());

        // set the icon
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("images/icon.png"))));

        // set the scene title
        stage.setTitle("Reader archive");

        // set the scene
        stage.setScene(scene);

        // show the stage
        stage.show();

        stage.setResizable(false);

        stage.setOnCloseRequest(event -> {
            Platform.exit();
            System.exit(200);
        });

    }

    // launch the program
    public static void main(String[] args) {
        // launch the program
        launch();
    }



}
