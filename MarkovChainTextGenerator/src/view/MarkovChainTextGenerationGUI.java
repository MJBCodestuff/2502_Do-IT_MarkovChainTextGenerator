package view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MarkovChainTextGenerationGUI extends Application {

    private int xResolution = 800;
    private int yResolution = 600;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("MarkovChains.fxml"));
            primaryStage.setTitle("Markov Chains");
            primaryStage.setScene(new Scene(root, xResolution, yResolution));
            primaryStage.show();
        } catch (IOException throwable) {
            throwable.printStackTrace();
        }
    }
}
