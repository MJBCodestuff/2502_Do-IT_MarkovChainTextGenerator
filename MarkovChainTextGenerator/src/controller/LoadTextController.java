package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import model.DatabaseConnector;

import java.util.Arrays;

public class LoadTextController {

    @FXML
    private ComboBox<String> loadText_TextChoice;
    private ObservableList<String> availableTexts = FXCollections.observableArrayList();
    private DatabaseConnector dbCon;
    private MarkovChainTextGenerationGUIController mainController;

    /**
     * sets the load text window to its initial state
     *
     * @param mainController the controller of the main window
     */
    public void initialize(MarkovChainTextGenerationGUIController mainController) {
        this.mainController = mainController;
        this.dbCon = mainController.getDbCon();
    }

    /**
     * sets the current text in the main controller to null and lists all available texts in the database
     * in the load text window combo box
     */
    public void listAvailableTexts() {
        mainController.setCurrentText(null);
        availableTexts.clear();
        String[] texts = dbCon.listAvailableTexts().split("\\r?\\n");
        availableTexts.addAll(Arrays.asList(texts));
        loadText_TextChoice.setItems(availableTexts);
    }

    /**
     * sets the current text in the main controller to the choice from the load text window combo box
     */
    public void setCurrentText() {
        if (loadText_TextChoice.getValue() != null) {
            mainController.setCurrentText(dbCon.loadText(loadText_TextChoice.getValue()));
        }
    }

    /**
     * if the user has chosen a text to load this calls the the show loaded text method in the main window and closes
     * the load text window
     * @param event button press event to track which window to close
     */
    public void showLoadedText(ActionEvent event) {
        if (mainController.getCurrentText() != null) {
            mainController.showLoadedText();
            closeWindow(event);
        } else {
            mainController.openGeneralPopup("Bitte wählen Sie einen Text aus.");
        }

    }

    /**
     * closes the load text controller window
     * @param event button press to track the window to close
     */
    public void closeWindow(ActionEvent event) {
        ((Node) (event.getSource())).getScene().getWindow().hide();
    }
}
